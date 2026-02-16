(ns escriba.models.document
  (:require [escriba.models.command :as models.command]
            [schema.core :as s])
  (:import (java.time Instant)))

(def statuses #{:requested :pending :completed :failed})
(def Status (apply s/enum statuses))

(s/defschema Document
  {:id                            s/Uuid
   :status                        Status
   :created-at                    Instant
   (s/optional-key :commands)     [models.command/Command]
   (s/optional-key :retrieved-at) Instant
   (s/optional-key :completed-at) Instant
   (s/optional-key :failed-at)    Instant})
