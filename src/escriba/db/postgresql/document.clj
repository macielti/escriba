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
  [{:keys [id status created-at commands retrieved-at]} :- models.document/Document
   postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (pg/with-transaction [database-conn' database-conn {:isolation-level :serializable}]
      (let [document (->> (pg/execute database-conn
                                      "INSERT INTO documents (id, status, created_at, retrieved_at) VALUES ($1, $2, $3, $4)
                                      RETURNING *"
                                      {:params [id (name status) created-at retrieved-at]
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

(s/defn pending-too-long :- [models.document/Document]
  [postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (->> (pg/execute database-conn
                     "SELECT * FROM documents
                      WHERE status = $1
                        AND retrieved_at < (NOW() - INTERVAL '1 minute')
                      ORDER BY retrieved_at ASC"
                     {:params [(name :pending)]})
         (map #(medley/remove-vals nil? %))
         (map #(adapters.document/postgresql->internal %)))))

(s/defn back-to-queue! :- (s/maybe models.document/Document)
  [document-id :- s/Uuid
   postgresql-pool]
  (pg/with-connection [database-conn postgresql-pool]
    (some->> (pg/execute database-conn
                         "UPDATE documents SET status = $2, retrieved_at = $4 WHERE id = $1 AND status = $3
                         RETURNING *"
                         {:params [document-id (name :requested) (name :pending) nil]
                          :first  true})
             (medley/remove-vals nil?)
             adapters.document/postgresql->internal)))
