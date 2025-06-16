(ns escriba.db.postgresql.document-test
  (:require [clojure.test :refer :all]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.db.postgresql.document :as database.document]
            [escriba.models.document :as models.document]
            [fixtures]
            [integration.aux.components :as aux.components]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

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
          _document (database.document/insert! fixtures/internal-document pool)
          requested-document (helpers.schema/generate models.document/Document {:status :requested})]
      (is (nil? (database.document/find-oldest-requested-document pool)))

      (database.document/insert! requested-document pool)

      (is (match? {:id     uuid?
                   :status :requested}
                  (database.document/find-oldest-requested-document pool))))))
