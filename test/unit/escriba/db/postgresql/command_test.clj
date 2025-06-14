(ns escriba.db.postgresql.command-test
  (:require [clojure.test :refer :all]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [escriba.db.postgresql.command :as database.command]
            [escriba.db.postgresql.document :as database.document]
            [fixtures]
            [integration.aux.components :as aux.components]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest find-by-document-id-test
  (testing "Should be able to find commands by document id"
    (let [pool (component.postgresql-mock/postgresql-pool-mock aux.components/schemas)
          {document-id :id} (database.document/insert! fixtures/internal-document pool)]
      (is (match? [{:document-id fixtures/document-id
                    :type        :feed-paper}]
                  (database.command/find-by-document-id document-id pool)))

      (is (match? []
                  (database.command/find-by-document-id (random-uuid) pool))))))
