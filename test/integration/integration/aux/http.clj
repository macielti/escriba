(ns integration.aux.http
  (:require [cheshire.core :as json]
            [clojure.test :refer :all]
            [io.pedestal.test :as test]))

(defn create-document!
  [document
   service-fn]
  (let [{:keys [status]} (test/response-for service-fn
                                            :post "/api/documents"
                                            :headers {"Content-Type" "application/json"}
                                            :body (json/encode {:document document}))]
    {:status status}))

(defn fetch-document!
  [service-fn]
  (let [{:keys [status body]} (test/response-for service-fn
                                                 :get "/api/documents")]
    {:status status
     :body   (json/decode body true)}))
