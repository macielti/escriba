(ns integration.aux.http
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [io.pedestal.connector.test :as test]))

(defn create-document!
  [document
   connection]
  (let [{:keys [status]} (test/response-for connection
                                            :post "/api/documents"
                                            :headers {:content-type "application/json"}
                                            :body (json/encode {:document document}))]
    {:status status}))

(defn fetch-document!
  [connection]
  (let [{:keys [status body]} (test/response-for connection
                                                 :get "/api/documents")]
    {:status status
     :body   (json/decode body true)}))

(defn document-ack!
  [document-id
   connection]
  (let [{:keys [status body]} (test/response-for connection
                                                 :put (str "/api/documents/" document-id "/ack"))]
    {:status status
     :body   (json/decode body true)}))
