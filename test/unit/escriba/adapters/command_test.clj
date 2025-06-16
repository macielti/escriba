(ns escriba.adapters.command-test
  (:require [clojure.test :refer :all]
            [escriba.adapters.command :as adapters.command]
            [fixtures]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest wire->internal-test
  (testing "Given a wire command, it returns an internal command"
    (is (match? {:id          uuid?
                 :document-id fixtures/document-id
                 :index       0
                 :scan-lines  42
                 :type        :feed-paper}
                (adapters.command/wire->internal fixtures/wire-command fixtures/document-id))))

  (testing "Given a wire command, it returns an internal command (PrintText)"
    (is (match? {:id          uuid?
                 :document-id fixtures/document-id
                 :index       0
                 :content     fixtures/text-content
                 :type        :print-text}
                (adapters.command/wire->internal fixtures/wire-print-text-command fixtures/document-id)))))

(s/deftest postgresql->internal-test
  (testing "Given a postgresql command, it returns an internal command"
    (is (match? {:id          uuid?
                 :document-id uuid?
                 :index       int?
                 :scan-lines  int?
                 :type        :feed-paper}
                (adapters.command/postgresql->internal fixtures/database-command))))

  (testing "Given a postgresql command, it returns an internal command (PrintText)"
    (is (match? {:id          uuid?
                 :document-id uuid?
                 :index       int?
                 :content     fixtures/text-content
                 :type        :print-text}
                (adapters.command/postgresql->internal fixtures/database-print-text-command)))))
