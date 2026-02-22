(ns escriba.adapters.command-test
  (:require [clojure.test :refer [is testing]]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.adapters.command :as adapters.command]
            [escriba.models.command :as models.command]
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

(s/deftest command->datalevin-test
  (testing "[Feed Paper] Given a internal command, it returns an datalevin command to be persisted"
    (let [internal-command (helpers.schema/generate models.command/FeedPaper {})]
      (is (match? {:command/id          uuid?
                   :command/document-id fixtures.document/document-id
                   :command/index       integer?
                   :command/lines       integer?
                   :command/type        :command.type/feed-paper}
                  (adapters.command/command->datalevin internal-command fixtures.document/document-id)))))

  (testing "[Print Text] Given a internal command, it returns an datalevin command to be persisted"
    (let [internal-command (helpers.schema/generate models.command/PrintText {})]
      (is (match? {:command/id          uuid?
                   :command/document-id fixtures.document/document-id
                   :command/index       integer?
                   :command/text        string?
                   :command/type        :command.type/print-text}
                  (adapters.command/command->datalevin internal-command fixtures.document/document-id)))))

  (testing "[Cut] Given a internal command, it returns an datalevin command to be persisted"
    (let [internal-command (helpers.schema/generate models.command/Cut {})]
      (is (match? {:command/id          uuid?
                   :command/document-id fixtures.document/document-id
                   :command/index       integer?
                   :command/type        :command.type/cut}
                  (adapters.command/command->datalevin internal-command fixtures.document/document-id)))))

  (testing "[Align] Given a internal command, it returns an datalevin command to be persisted"
    (let [internal-command (helpers.schema/generate models.command/Align {})]
      (is (match? {:command/id          uuid?
                   :command/document-id fixtures.document/document-id
                   :command/index       integer?
                   :command/orientation keyword?
                   :command/type        :command.type/align}
                  (adapters.command/command->datalevin internal-command fixtures.document/document-id)))))

  (testing "[Size] Given a internal command, it returns an datalevin command to be persisted"
    (let [internal-command (helpers.schema/generate models.command/Size {})]
      (is (match? {:command/id          uuid?
                   :command/document-id fixtures.document/document-id
                   :command/index       integer?
                   :command/size        keyword?
                   :command/type        :command.type/size}
                  (adapters.command/command->datalevin internal-command fixtures.document/document-id)))))

  (testing "[Style] Given a internal command, it returns an datalevin command to be persisted"
    (let [internal-command (helpers.schema/generate models.command/Style {})]
      (is (match? {:command/id          uuid?
                   :command/document-id fixtures.document/document-id
                   :command/index       integer?
                   :command/style       keyword?
                   :command/type        :command.type/style}
                  (adapters.command/command->datalevin internal-command fixtures.document/document-id))))))
