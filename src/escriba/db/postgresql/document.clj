(ns escriba.db.postgresql.document
  (:require [escriba.adapters.document :as adapters.document]
            [escriba.db.postgresql.command :as database.command]
            [escriba.logic.document :as logic.document]
            [escriba.models.document :as models.document]
            [medley.core :as medley]
            [pg.core :as pg]
            [schema.core :as s])
  (:import (java.util Date)))

(s/defn insert! :- models.document/Document
  [{:keys [id status created-at commands]} :- models.document/Document
   postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (pg/with-transaction [database-conn' database-conn {:isolation-level :serializable}]
      (let [document (->> (pg/execute database-conn
                                      "INSERT INTO documents (id, status, created_at) VALUES ($1, $2, $3)
                                      RETURNING *"
                                      {:params [id (name status) created-at]
                                       :first  true})
                          (medley/remove-vals nil?)
                          adapters.document/postgresql->internal)
            commands (mapv #(database.command/insert-using-connection! % database-conn') commands)]
        (logic.document/document-with-commands document commands)))))

(s/defn find-oldest-requested-document :- (s/maybe models.document/Document)
  [postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (some->> (pg/execute database-conn
                         "SELECT * FROM documents WHERE status = $1 ORDER BY created_at ASC LIMIT 1"
                         {:params ["requested"]
                          :first  true})
             (medley/remove-vals nil?)
             adapters.document/postgresql->internal)))

(s/defn pending! :- (s/maybe models.document/Document)
  [document-id :- s/Uuid
   postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (some->> (pg/execute database-conn
                         "UPDATE documents SET status = $2, retrieved_at = $4 WHERE id = $1 AND status = $3
                         RETURNING *"
                         {:params [document-id (name :pending) (name :requested) (Date.)]
                          :first  true})
             (medley/remove-vals nil?)
             adapters.document/postgresql->internal)))

(s/defn failed! :- (s/maybe models.document/Document)
  [document-id :- s/Uuid
   postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (some->> (pg/execute database-conn
                         "UPDATE documents SET status = $2, failed_at = $4 WHERE id = $1 AND status = $3
                         RETURNING *"
                         {:params [document-id (name :failed) (name :pending) (Date.)]
                          :first  true})
             (medley/remove-vals nil?)
             adapters.document/postgresql->internal)))

(s/defn completed! :- (s/maybe models.document/Document)
  [document-id :- s/Uuid
   postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (some->> (pg/execute database-conn
                         "UPDATE documents SET status = $2, completed_at = $4 WHERE id = $1 AND status = $3
                         RETURNING *"
                         {:params [document-id (name :completed) (name :pending) (Date.)]
                          :first  true})
             (medley/remove-vals nil?)
             adapters.document/postgresql->internal)))
