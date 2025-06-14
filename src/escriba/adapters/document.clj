(ns escriba.adapters.document
  (:require [escriba.adapters.command :as adapters.command]
            [escriba.models.document :as models.document]
            [escriba.wire.in.document :as wire.in.document]
            [schema.core :as s])
  (:import (java.util Date)))

(s/defn wire->internal :- models.document/Document
  [{:keys [commands]} :- wire.in.document/Document]
  (let [document-id (random-uuid)
        commands' (map #(adapters.command/wire->internal % document-id) commands)]
    {:id         document-id
     :status     :requested
     :created-at (Date.)
     :commands   commands'}))
