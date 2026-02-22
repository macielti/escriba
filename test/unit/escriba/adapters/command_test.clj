(ns escriba.adapters.command-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.adapters.command :as adapters.command]
            [escriba.wire.in.command :as wire.in.command]
            [fixtures.document]
            [matcher-combinators.test :refer [match?]]
            [schema.test :as s]))

(s/deftest wire->internal-test
  (testing "[Feed Paper] Given a wire command, it returns an internal command"
    (let [wire-command (helpers.schema/generate wire.in.command/FeedPaper {})]
      (is (match? {:id          uuid?
                   :document-id fixtures.document/document-id
                   :index       integer?
                   :lines       integer?
                   :type        :feed-paper}
                  (adapters.command/wire->internal wire-command fixtures.document/document-id)))))

  (testing "[Print Text] Given a wire command, it returns an internal command"
    (let [wire-command (helpers.schema/generate wire.in.command/PrintText {})]
      (is (match? {:id          uuid?
                   :document-id fixtures.document/document-id
                   :index       integer?
                   :text        string?
                   :type        :print-text}
                  (adapters.command/wire->internal wire-command fixtures.document/document-id)))))

  (testing "[Cut] Given a wire command, it returns an internal command"
    (let [wire-command (helpers.schema/generate wire.in.command/Cut {})]
      (is (match? {:id          uuid?
                   :document-id fixtures.document/document-id
                   :index       integer?
                   :type        :cut}
                  (adapters.command/wire->internal wire-command fixtures.document/document-id)))))

  (testing "[Align] Given a wire command, it returns an internal command"
    (let [wire-command (helpers.schema/generate wire.in.command/Align {})]
      (is (match? {:id          uuid?
                   :document-id fixtures.document/document-id
                   :index       integer?
                   :orientation keyword?
                   :type        :align}
                  (adapters.command/wire->internal wire-command fixtures.document/document-id)))))

  (testing "[Size] Given a wire command, it returns an internal command"
    (let [wire-command (helpers.schema/generate wire.in.command/Size {})]
      (is (match? {:id          uuid?
                   :document-id fixtures.document/document-id
                   :index       integer?
                   :size        keyword?
                   :type        :size}
                  (adapters.command/wire->internal wire-command fixtures.document/document-id)))))

  (testing "[Style] Given a wire command, it returns an internal command"
    (let [wire-command (helpers.schema/generate wire.in.command/Style {})]
      (is (match? {:id          uuid?
                   :document-id fixtures.document/document-id
                   :index       integer?
                   :style       keyword?
                   :type        :style}
                  (adapters.command/wire->internal wire-command fixtures.document/document-id))))))
