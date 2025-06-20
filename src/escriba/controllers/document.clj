(ns escriba.controllers.document
  (:require [escriba.db.postgresql.command :as database.command]
            [escriba.db.postgresql.document :as database.document]
            [escriba.logic.document :as logic.document]
            [escriba.models.document :as models.document]
            [schema.core :as s]))

(s/defn create! :- models.document/Document
  [document :- models.document/Document
   postgresql :- s/Any]
  (database.document/insert! document postgresql))

(s/defn retrieve-document-to-be-printed :- (s/maybe models.document/Document)
  [postgresql :- s/Any]
  (let [{document-id :id} (database.document/find-oldest-requested-document postgresql)
        commands (delay (database.command/find-by-document-id document-id postgresql))]
    (when document-id
      (-> (database.document/pending! document-id postgresql)
          (logic.document/document-with-commands @commands)))))

(s/defn acknowledge-document! :- (s/maybe models.document/Document)
  [document-id :- s/Uuid
   postgresql :- s/Any]
  (database.document/completed! document-id postgresql))

(s/defn back-to-queue!
  [postgresql :- s/Any]
  (let [documents (database.document/pending-too-long postgresql)]
    (doseq [{:keys [id]} documents]
      (database.document/back-to-queue! id postgresql))))
