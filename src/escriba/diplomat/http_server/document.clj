(ns escriba.diplomat.http-server.document
  (:require [escriba.adapters.document :as adapters.document]
            [escriba.controllers.document :as controllers.document]
            [schema.core :as s])
  (:import (java.util UUID)))

(s/defn create!
  [{{:keys [document]}  :json-params
    {:keys [datalevin]} :components}]
  (-> (adapters.document/wire->internal document)
      (controllers.document/create! datalevin))
  {:status 202
   :body   {}})

(s/defn retrieve-document-to-be-printed!
  [{{:keys [datalevin]} :components}]
  {:status 200
   :body   (if-let [document (controllers.document/retrieve-document-to-be-printed datalevin)]
             {:documents [document]}
             {:documents []})})

(s/defn acknowledge!
  [{{:keys [document-id]} :path-params
    {:keys [datalevin]}   :components}]
  {:status 200
   :body   {:document (controllers.document/acknowledge-document! (UUID/fromString document-id) datalevin)}})
