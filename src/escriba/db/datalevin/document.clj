(ns escriba.db.datalevin.document
  (:require [common-clj.time.parser :as time.parser]
            [datalevin.core :as d]
            [escriba.adapters.command :as adapters.command]
            [escriba.adapters.document :as adapters.document]
            [escriba.logic.document :as logic.document]
            [escriba.models.document :as models.document]
            [java-time.api :as jt]
            [schema.core :as s]))

(s/defn lookup-document-with-commands :- models.document/Document
  [document-id :- s/Uuid
   db-after]
  (let [document-without-commands (-> (d/q '[:find (pull ?document [*])
                                             :in $ ?document-id
                                             :where [?document :document/id ?document-id]] db-after document-id)
                                      ffirst
                                      (dissoc :db/id)
                                      adapters.document/datalevin->document)
        commands (->> (d/q '[:find (pull ?command [*])
                             :in $ ?document-id
                             :where [?command :command/document-id ?document-id]] db-after document-id)
                      (mapv #(-> % first (dissoc :db/id)))
                      (mapv adapters.command/datalevin->command))]
    (logic.document/document-with-commands document-without-commands commands)))

(s/defn insert-document-with-commands! :- models.document/Document
  [{:keys [commands id] :as document} :- models.document/Document
   database]
  (let [document' (adapters.document/document->datalevin document)
        commands' (map #(adapters.command/command->datalevin % id) commands)
        {:keys [db-after]} (->> (conj [] document' commands') flatten (d/transact! database))]
    (lookup-document-with-commands id db-after)))

(s/defn find-oldest-requested-document :- (s/maybe models.document/Document)
  [database]
  (let [documents (some->> (d/q '[:find (pull ?document [*])
                                  :in $
                                  :where [?document :document/id _]
                                  [?document :document/status :document.status/requested]] (d/db database))
                           (mapv #(-> % first (dissoc :db/id)))
                           (mapv adapters.document/datalevin->document))]
    (when documents
      (-> (sort-by :document/created-at #(compare %2 %1) documents) last))))

(s/defn set-as-pending! :- models.document/Document
  [document-id :- s/Uuid
   database]
  (let [{:keys [db-after]} (d/transact! database [{:document/id           document-id
                                                   :document/status       :document.status/pending
                                                   :document/retrieved-at (-> (jt/instant) time.parser/instant->legacy-date)}])]
    (-> (d/q '[:find (pull ?document [*])
               :in $ ?document-id
               :where [?document :document/id ?document-id]] db-after document-id)
        ffirst
        (dissoc :db/id)
        adapters.document/datalevin->document)))

(s/defn set-as-completed! :- models.document/Document
  [document-id :- s/Uuid
   database]
  (let [{:keys [db-after]} (d/transact! database [{:document/id           document-id
                                                   :document/status       :document.status/completed
                                                   :document/completed-at (-> (jt/instant) time.parser/instant->legacy-date)}])]
    (-> (d/q '[:find (pull ?document [*])
               :in $ ?document-id
               :where [?document :document/id ?document-id]] db-after document-id)
        ffirst
        (dissoc :db/id)
        adapters.document/datalevin->document)))

(s/defn pending-for-too-long! :- [models.document/Document]
  [database]
  (let [documents (some->> (d/q '[:find (pull ?document [*])
                                  :in $
                                  :where [?document :document/status :document.status/pending]] (d/db database))
                           (mapv #(-> % first (dissoc :db/id)))
                           (mapv adapters.document/datalevin->document))]
    (when documents
      (filter #(>= (jt/time-between (:retrieved-at %) (jt/instant) :seconds) 60) documents))))

(s/defn back-to-queue! :- models.document/Document
  [document-id :- s/Uuid
   database]
  (let [{:keys [db-after]} (d/transact! database [{:document/id     document-id
                                                   :document/status :document.status/requested}])]
    (-> (d/q '[:find (pull ?document [*])
               :in $ ?document-id
               :where [?document :document/id ?document-id]] db-after document-id)
        ffirst
        (dissoc :db/id)
        adapters.document/datalevin->document)))
