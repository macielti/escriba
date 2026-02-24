(ns escriba.logic.document-test
  (:require [clojure.test :refer :all]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.logic.document :as logic.document]
            [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [java-time.api :as jt]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest document-with-commands-test
  (testing "Given a document model and a list of commands we should be able to convert to a document map with commands"
    (let [internal-commands [(helpers.schema/generate models.command/Command {:type :cut})]
          internal-document (helpers.schema/generate models.document/Document {})]
      (is (match? {:id         uuid?
                   :status     keyword?
                   :created-at jt/instant?
                   :commands   [{:id          uuid?
                                 :index       integer?
                                 :document-id uuid?
                                 :type        keyword?}]}
                  (logic.document/document-with-commands internal-document internal-commands))))))
