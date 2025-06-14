(ns escriba.adapters.document
  (:require [escriba.adapters.command :as adapters.command]
            [escriba.models.document :as models.document]
            [escriba.wire.in.document :as wire.in.document]
            [escriba.wire.postgresql.document :as wire.postgresql.document]
            [medley.core :as medley]
            [schema.core :as s])
  (:import (java.time ZoneOffset)
           (java.util Date)))

(s/defn wire->internal :- models.document/Document
  [{:keys [commands]} :- wire.in.document/Document]
  (let [document-id (random-uuid)
        commands' (map #(adapters.command/wire->internal % document-id) commands)]
    {:id         document-id
     :status     :requested
     :created-at (Date.)
     :commands   commands'}))

(defn local-datetime->utc-instant [ldt]
  (Date/from (.toInstant (.atZone ldt ZoneOffset/UTC))))

(s/defn postgresql->internal :- models.document/Document
  [{:keys [id status created_at retrieved_at completed_at failed_at]} :- wire.postgresql.document/Document]
  (medley/assoc-some {:id         id
                      :status     (keyword status)
                      :created-at (local-datetime->utc-instant created_at)
                      :commands   []}
                     :retrieved-at retrieved_at
                     :completed-at completed_at
                     :failed-at failed_at))
