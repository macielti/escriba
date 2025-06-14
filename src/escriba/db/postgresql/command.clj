(ns escriba.db.postgresql.command
  (:require [escriba.adapters.command :as adapters.command]
            [escriba.models.command :as models.command]
            [medley.core :as medley]
            [pg.core :as pg]
            [schema.core :as s]))

(s/defn insert-using-connection! :- models.command/Command
  [{:keys [id index document-id type scan-lines]} :- models.command/Command
   postgresql-connection]
  (->> (pg/execute postgresql-connection
                   "INSERT INTO commands (id, index, document_id, type, scan_lines) VALUES ($1, $2, $3, $4, $5)
                   RETURNING *"
                   {:params [id index document-id (name type) scan-lines]
                    :first  true})
       (medley/remove-vals nil?)
       adapters.command/postgresql->internal))
