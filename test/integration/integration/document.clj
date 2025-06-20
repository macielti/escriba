(ns integration.document
  (:require [clojure.test :refer :all]
            [fixtures]
            [integrant.core :as ig]
            [integration.aux.components :as aux.components]
            [integration.aux.http :as aux.http]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]
            [service-component.core :as component.service]))

(s/deftest create-document
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)]

    (testing "Should create a document with a valid request"
      (is (= {:status 202}
             (aux.http/create-document! fixtures/wire-document service-fn))))

    (ig/halt! system)))

(s/deftest retrieve-document-to-be-printed
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)]

    (testing "Should create a document with a valid request"
      (is (= {:status 202}
             (aux.http/create-document! fixtures/wire-document service-fn))))

    (testing "Should be able to trieve the document to be printed"
      (is (match? {:status 200
                   :body   {:document {:commands [{:id   string?
                                                   :type string?}]
                                       :id       string?
                                       :status   "pending"}}}
                  (aux.http/fetch-document! service-fn))))

    (ig/halt! system)))

(s/deftest document-acknowledge
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)
        document-creation-response (aux.http/create-document! fixtures/wire-document service-fn)
        fetch-document-response (aux.http/fetch-document! service-fn)]

    (testing "Should create a document with a valid request"
      (is (= {:status 202}
             document-creation-response)))

    (testing "Should fetch the document to be printed"
      (is (match? {:status 200
                   :body   {:document {:status "pending"}}}
                  fetch-document-response)))

    (testing "Should be able to acknowledge the document"
      (is (match? {:status 200
                   :body   {:document {:status "completed"}}}
                  (aux.http/document-ack! (get-in fetch-document-response [:body :document :id]) service-fn))))

    (ig/halt! system)))

(s/deftest back-to-queue
  (let [system (aux.components/start-system!)
        service-fn (-> system ::component.service/service :io.pedestal.http/service-fn)
        document-creation-response (aux.http/create-document! fixtures/wire-document service-fn)
        fetch-document-response (aux.http/fetch-document! service-fn)]

    (testing "Should create a document with a valid request"
      (is (= {:status 202}
             document-creation-response)))

    (testing "Should fetch the document to be printed"
      (is (match? {:status 200
                   :body   {:document {:status "pending"}}}
                  fetch-document-response)))

    (testing "Second attempt to fetch document should return the nothing"
      (is (match? {:status 200
                   :body   {}}
                  (aux.http/fetch-document! service-fn))))

    (Thread/sleep 90000)

    (testing "Should fetch the document to be printed, after it is sent back to the queue"
      (is (match? {:status 200
                   :body   {:document {:status "pending"}}}
                  fetch-document-response)))

    (ig/halt! system)))
