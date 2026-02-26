(ns escriba.db.datalevin.document-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.helpers.schema :as helpers.schema]
            [datalevin.core :as d]
            [datalevin.mock :as database.mock]
            [escriba.db.datalevin.config :as database.config]
            [escriba.db.datalevin.document :as database.document]
            [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [fixtures.document]
            [java-time.api :as jt]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest insert-document-with-commands!-test
  (testing "We should be able to persist a Document entity model with it's documents"
    (let [database-connection (database.mock/database-connection-for-unit-tests! database.config/schema)
          internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:id       fixtures.document/document-id
                                                                               :commands internal-commands})]
      (is (match? {:commands   [{:document-id fixtures.document/document-id
                                 :id          uuid?
                                 :index       integer?
                                 :type        :cut}]
                   :created-at jt/instant?
                   :id         fixtures.document/document-id
                   :status     keyword?}
                  (database.document/insert-document-with-commands! internal-document database-connection))))))

(s/deftest lookup-document-with-commands-test
  (testing "We should be able query Document with Commands by document-id"
    (let [database-connection (database.mock/database-connection-for-unit-tests! database.config/schema)
          internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:id       fixtures.document/document-id
                                                                               :commands internal-commands})
          _ (database.document/insert-document-with-commands! internal-document database-connection)]
      (is (match? {:commands   [{:document-id fixtures.document/document-id
                                 :id          uuid?
                                 :index       integer?
                                 :type        :cut}]
                   :created-at jt/instant?
                   :id         fixtures.document/document-id
                   :status     keyword?}
                  (database.document/lookup-document-with-commands fixtures.document/document-id (d/db database-connection)))))))

(s/deftest find-oldest-requested-document-test
  (testing "We should be able query the oldest requested document entity, without commands"
    (let [database-connection (database.mock/database-connection-for-unit-tests! database.config/schema)
          internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:id         fixtures.document/document-id
                                                                               :commands   internal-commands
                                                                               :status     :requested
                                                                               :created-at (jt/instant 0)})
          internal-document-ii (helpers.schema/generate models.document/Document {:id         (random-uuid)
                                                                                  :commands   internal-commands
                                                                                  :status     :requested
                                                                                  :created-at (jt/instant 10)})
          internal-document-iii (helpers.schema/generate models.document/Document {:id         (random-uuid)
                                                                                   :commands   internal-commands
                                                                                   :status     :requested
                                                                                   :created-at (jt/instant 20)})
          _ (database.document/insert-document-with-commands! internal-document database-connection)
          _ (database.document/insert-document-with-commands! internal-document-ii database-connection)
          _ (database.document/insert-document-with-commands! internal-document-iii database-connection)]

      (is (match? {:created-at jt/instant?
                   :id         fixtures.document/document-id
                   :status     :requested}
                  (database.document/find-oldest-requested-document database-connection))))))

(s/deftest set-as-pending!-test
  (testing "We should be able to mark a document as pending"
    (let [database-connection (database.mock/database-connection-for-unit-tests! database.config/schema)
          internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:id         fixtures.document/document-id
                                                                               :commands   internal-commands
                                                                               :status     :requested
                                                                               :created-at (jt/instant 0)})
          _ (database.document/insert-document-with-commands! (dissoc internal-document :retrieved-at) database-connection)]

      (is (match? {:id           fixtures.document/document-id
                   :status       :pending
                   :retrieved-at jt/instant?}
                  (database.document/set-as-pending! fixtures.document/document-id database-connection))))))

(s/deftest back-to-queue!-test
  (testing "We should be able to move a document back to requested status"
    (let [database-connection (database.mock/database-connection-for-unit-tests! database.config/schema)
          internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:id       fixtures.document/document-id
                                                                               :commands internal-commands
                                                                               :status   :requested})
          _ (database.document/insert-document-with-commands! internal-document database-connection)
          _ (database.document/set-as-pending! fixtures.document/document-id database-connection)]

      (is (match? {:id     fixtures.document/document-id
                   :status :requested}
                  (database.document/back-to-queue! fixtures.document/document-id database-connection))))))
