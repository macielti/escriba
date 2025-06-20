(ns escriba.db.postgresql.document-test
  (:require [clojure.test :refer :all]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.db.postgresql.document :as database.document]
            [escriba.models.document :as models.document]
            [fixtures]
            [integration.aux.components :as aux.components]
            [matcher-combinators.matchers :as matchers]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s])
  (:import (java.util Date)))

(s/deftest insert-test
  (testing "Should insert a Document inside Database"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)]
      (is (match? {:id       uuid?
                   :commands vector?}
                  (database.document/insert! fixtures/internal-document pool))))))

(s/deftest insert-print-text-command-test
  (testing "Should insert a Document inside Database, with the PrintText command"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)]
      (is (match? {:id       uuid?
                   :commands [{:type    :print-text
                               :content fixtures/text-content}]}
                  (database.document/insert! (assoc fixtures/internal-document :commands [fixtures/internal-print-text-command]) pool))))))

(s/deftest find-oldest-requested-document-test
  (testing "Should find the oldest requested document"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)
          requested-document (helpers.schema/generate models.document/Document {:status :requested})]
      (is (nil? (database.document/find-oldest-requested-document pool)))

      (database.document/insert! requested-document pool)

      (is (match? {:id     uuid?
                   :status :requested}
                  (database.document/find-oldest-requested-document pool))))))

(s/deftest update-status-test
  (testing "Should find the oldest requested document"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)
          requested-document (helpers.schema/generate models.document/Document {:status :requested})
          document (database.document/insert! requested-document pool)]

      (is (match? {:id     (:id document)
                   :status :requested}
                  document))

      (is (match? {:id           (:id document)
                   :status       :pending
                   :retrieved-at inst?}
                  (database.document/pending! (:id document) pool)))

      (is (match? {:id           (:id document)
                   :status       :completed
                   :completed-at inst?}
                  (database.document/completed! (:id document) pool)))

      (is (nil? (database.document/failed! (:id document) pool))))))

(s/deftest pending-too-long-test
  (testing "Should be able to find pending documents that are pending for too long"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)
          requested-document (helpers.schema/generate models.document/Document {:status       :pending
                                                                                :retrieved-at (Date.)})
          {:keys [id]} (database.document/insert! requested-document pool)]

      (is (= []
             (database.document/pending-too-long pool)))

      (Thread/sleep 90000)

      (is (match? [{:id     id
                    :status :pending}]
                  (database.document/pending-too-long pool))))))

(s/deftest back-to-queue-test
  (testing "Should be able to move a pending document back to the queue"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)
          requested-document (helpers.schema/generate models.document/Document {:status       :pending
                                                                                :retrieved-at (Date.)})
          {:keys [id] :as document} (database.document/insert! requested-document pool)]

      (is (match? {:status       :pending
                   :retrieved-at inst?}
                  document))

      (is (match? (matchers/equals {:id         id
                                    :commands   []
                                    :created-at inst?
                                    :status     :requested})
                  (database.document/back-to-queue! id pool))))))
