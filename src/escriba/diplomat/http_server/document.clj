(ns escriba.diplomat.http-server.document
  (:require [escriba.adapters.document :as adapters.document]
            [escriba.controllers.document :as controllers.document]
            [schema.core :as s]))

(s/defn create!
  [{{:keys [document]}   :json-params
    {:keys [postgresql]} :components}]
  (-> (adapters.document/wire->internal document)
      (controllers.document/create! postgresql))
  {:status 202
   :body   {}})

(s/defn retrieve-document-to-be-printed!
  [{{:keys [postgresql]} :components}]
  {:status 200
   :body   {:document (controllers.document/retrieve-document-to-be-printed postgresql)}})
