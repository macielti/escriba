(ns escriba.wire.datalevin.document
  (:require [schema.core :as s]))

(def document
  {:document/id           {:db/valueType :db.type/uuid
                           :db/unique    :db.unique/identity}
   :document/status       {:db/valueType :db.type/keyword}
   :document/created-at   {:db/valueType :db.type/instant}
   :document/retrieved-at {:db/valueType :db.type/instant}
   :document/completed-at {:db/valueType :db.type/instant}
   :document/failed-at    {:db/valueType :db.type/instant}})

(def statuses #{:document.status/requested
                :document.status/pending
                :document.status/completed
                :document.status/failed})
(def Status (apply s/enum statuses))

(def Document
  {:document/id                            s/Uuid
   :document/status                        Status
   :document/created-at                    s/Inst
   (s/optional-key :document/retrieved-at) s/Inst
   (s/optional-key :document/completed-at) s/Inst
   (s/optional-key :document/failed-at)    s/Inst})
