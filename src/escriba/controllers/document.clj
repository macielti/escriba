(ns escriba.controllers.document
  (:require [escriba.db.datalevin.command :as database.command]
            [escriba.db.datalevin.document :as database.document]
            [escriba.logic.document :as logic.document]
            [escriba.models.document :as models.document]
            [schema.core :as s]))

(s/defn create! :- models.document/Document
  [document :- models.document/Document
   database :- s/Any]
  (database.document/insert-document-with-commands! document database))

(s/defn retrieve-document-to-be-printed :- (s/maybe models.document/Document)
  [database]
  (let [{document-id :id} (database.document/find-oldest-requested-document database)
        commands (delay (database.command/find-by-document-id document-id database))]
    (when document-id
      (-> (database.document/set-as-pending! document-id database)
          (logic.document/document-with-commands @commands)))))

(s/defn acknowledge-document! :- (s/maybe models.document/Document)
  [document-id :- s/Uuid
   datalevin]
  (database.document/set-as-completed! document-id datalevin))

(s/defn back-to-queue!
  [datalevin]
  (let [documents (database.document/pending-for-too-long! datalevin)]
    (doseq [{:keys [id]} documents]
      (database.document/back-to-queue! id datalevin))))
