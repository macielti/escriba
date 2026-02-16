(ns escriba.db.datalevin.command
  (:require [datalevin.core :as d]
            [escriba.adapters.command :as adapters.command]
            [escriba.models.command :as models.command]
            [schema.core :as s]))

(s/defn find-by-document-id :- [models.command/Command]
  [document-id :- s/Uuid
   database]
  (->> (d/q '[:find (pull ?command [*])
              :in $ ?document-id
              :where [?command :command/document-id ?document-id]] (d/db database) document-id)
       (mapv #(-> % first (dissoc :db/id)))
       (mapv adapters.command/datalevin->command)))