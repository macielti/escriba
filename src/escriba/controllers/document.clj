(ns escriba.controllers.document
  (:require [escriba.db.postgresql.document :as database.document]
            [escriba.db.postgresql.command :as database.command]
            [escriba.models.document :as models.document]
            [escriba.logic.document :as logic.document]
            [schema.core :as s]))

(s/defn create! :- models.document/Document
  [document :- models.document/Document
   postgresql :- s/Any]
  (database.document/insert! document postgresql))

(s/defn retrieve-document-to-be-printed :- models.document/Document
  [postgresql :- s/Any]
  (let [{document-id :id :as document} (database.document/find-oldest-requested-document postgresql)
        commands (database.command/find-by-document-id document-id postgresql)]
    (logic.document/document-with-commands document commands)))
