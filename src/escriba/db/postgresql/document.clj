(ns escriba.db.postgresql.document
  (:require [escriba.adapters.document :as adapters.document]
            [escriba.db.postgresql.command :as database.command]
            [escriba.logic.document :as logic.document]
            [escriba.models.document :as models.document]
            [medley.core :as medley]
            [pg.core :as pg]
            [schema.core :as s]))

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
