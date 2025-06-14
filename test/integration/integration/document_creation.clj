(ns integration.document-creation
  (:require [clojure.test :refer :all]
            [fixtures]
            [integrant.core :as ig]
            [integration.aux.components :as aux.components]
            [integration.aux.http :as aux.http]
            [schema.test :as s]
            [service-component.core :as component.service]))

(s/deftest create-document
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)]

    (testing "Should create a document with a valid request"
      (is (= {:status 202}
             (aux.http/create-document! fixtures/wire-document service-fn))))

    (ig/halt! system)))
