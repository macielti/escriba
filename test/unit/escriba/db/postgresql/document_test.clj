(ns escriba.db.postgresql.document-test
  (:require [clojure.test :refer :all]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [escriba.db.postgresql.document :as database.document]
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
