(ns escriba.db.datalevin.document-test
  (:require [clojure.test :refer :all]
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
