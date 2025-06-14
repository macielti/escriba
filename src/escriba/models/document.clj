(ns escriba.models.document
  (:require [schema.core :as s]))

(def statuses #{:requested :pending :completed :failed})
(def Status (apply s/enum statuses))

(s/defschema Document
  {:id                            s/Uuid
   :status                        Status
   :created-at                    s/Inst
   (s/optional-key :retrieved-at) s/Inst
   (s/optional-key :completed-at) s/Inst
   (s/optional-key :failed-at)    s/Inst})
