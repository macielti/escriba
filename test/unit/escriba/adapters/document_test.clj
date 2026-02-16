(ns escriba.adapters.document-test
  (:require [clojure.test :refer :all]
            [escriba.adapters.document :as adapters.document]
            [fixtures]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest wire->internal-test
  (testing "Given a wire document, it returns an internal document"
    (is (match? {:id         uuid?
                 :commands   [{:id   uuid?
                               :type :feed-paper}]
                 :created-at inst?
                 :status     :requested}
                (adapters.document/wire->internal fixtures/wire-document)))))
