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
                 :lines       42
                 :type        :feed-paper}
                (adapters.command/wire->internal fixtures/wire-command fixtures/document-id))))

  (testing "Given a wire command, it returns an internal command (PrintText)"
    (is (match? {:id          uuid?
                 :document-id fixtures/document-id
                 :index       0
                 :text        fixtures/text-content
                 :type        :print-text}
                (adapters.command/wire->internal fixtures/wire-print-text-command fixtures/document-id)))))
