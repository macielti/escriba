(ns escriba.controllers.document
  (:require [escriba.db.postgresql.document :as database.document]
            [escriba.models.document :as models.document]
            [schema.core :as s]))

(s/defn create! :- models.document/Document
  [document :- models.document/Document
   postgresql :- s/Any]
  (database.document/insert! document postgresql))
