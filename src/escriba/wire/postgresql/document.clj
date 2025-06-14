(ns escriba.wire.postgresql.document
  (:require [schema.core :as s])
  (:import (java.time LocalDateTime)))

(def statuses #{"requested" "pending" "completed" "failed"})
(def Status (apply s/enum statuses))

(s/defschema Document
  {:id                            s/Uuid
   :status                        Status
   :created_at                    LocalDateTime
   (s/optional-key :retrieved_at) LocalDateTime
   (s/optional-key :completed_at) LocalDateTime
   (s/optional-key :failed_at)    LocalDateTime})
