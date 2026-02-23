(ns escriba.adapters.document-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.adapters.document :as adapters.document]
            [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [fixtures.document]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest wire->internal-test
  (testing "Given a wire document, it returns an internal document"
    (is (match? {:id         uuid?
                 :commands   [{:id   uuid?
                               :type :feed-paper}]
                 :created-at inst?
                 :status     :requested}
                (adapters.document/wire->internal fixtures.document/wire-document)))))

(s/deftest document->datalevin-test
  (testing "Given a internal document we should be able to convert it to datalevin"
    (let [internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {:commands internal-commands})]
      (is (match? {:document/id         uuid?
                   :document/status     keyword?
                   :document/created-at inst?}
                  (adapters.document/document->datalevin internal-document))))))
