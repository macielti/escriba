(ns escriba.db.datalevin.command-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.helpers.schema :as helpers.schema]
            [datalevin.mock :as database.mock]
            [escriba.db.datalevin.command :as database.command]
            [escriba.db.datalevin.config :as database.config]
            [escriba.db.datalevin.document :as database.document]
            [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [fixtures.document]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest find-by-document-id-test
  (testing "We should be able query commands by document-id"
    (let [database-connection (database.mock/database-connection-for-unit-tests! database.config/schema)
          internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:id       fixtures.document/document-id
                                                                               :commands internal-commands})
          _ (database.document/insert-document-with-commands! internal-document database-connection)]
      (is (match? [{:document-id fixtures.document/document-id
                    :id          uuid?
                    :index       integer?
                    :type        :cut}]
                  (database.command/find-by-document-id fixtures.document/document-id database-connection)))

      (is (match? []
                  (database.command/find-by-document-id (random-uuid) database-connection))))))
