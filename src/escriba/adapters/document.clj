(ns escriba.adapters.document
  (:require [common-clj.time.parser :as time.parser]
            [common-clj.time.util :as time.util]
            [escriba.adapters.command :as adapters.command]
            [escriba.models.document :as models.document]
            [escriba.wire.datalevin.document :as wire.datalevin.document]
            [escriba.wire.in.document :as wire.in.document]
            [medley.core :as medley]
            [schema.core :as s]))

(s/defn wire->internal :- models.document/Document
  [{:keys [commands]} :- wire.in.document/Document]
  (let [document-id (random-uuid)
        commands' (mapv #(adapters.command/wire->internal % document-id) commands)]
    {:id         document-id
     :status     :requested
     :created-at (time.util/instant-now)
     :commands   commands'}))

(s/defn document->datalevin :- wire.datalevin.document/Document
  [{:keys [id status created-at failed-at retrieved-at completed-at] :as _document} :- models.document/Document]
  (medley/assoc-some {:document/id         id
                      :document/status     (keyword "document.status" (name status))
                      :document/created-at (time.parser/instant->legacy-date created-at)}
                     :document/retrieved-at (some-> retrieved-at time.parser/instant->legacy-date)
                     :document/completed-at (some-> completed-at time.parser/instant->legacy-date)
                     :document/failed-at (some-> failed-at time.parser/instant->legacy-date)))

(s/defn datalevin->document :- models.document/Document
  [{:document/keys [id status created-at retrieved-at completed-at failed-at]} :- wire.datalevin.document/Document]
  (medley/assoc-some {:id         id
                      :status     (-> status name keyword)
                      :created-at (time.parser/legacy-date->instant created-at)}
                     :retrieved-at (some-> retrieved-at time.parser/legacy-date->instant)
                     :completed-at (some-> completed-at time.parser/legacy-date->instant)
                     :failed-at (some-> failed-at time.parser/legacy-date->instant)))