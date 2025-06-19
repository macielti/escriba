(ns fixtures
  (:require [clojure.test :refer :all]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [escriba.wire.in.command :as wire.in.command]
            [escriba.wire.in.document :as wire.in.document]
            [escriba.wire.postgresql.command :as wire.postgresql.command]
            [escriba.wire.postgresql.document :as wire.postgresql.document]
            [schema.core :as s])
  (:import (java.util Date)))

(def document-id (random-uuid))
(def text-content "Lorem ipsum dolor sit amet.")

(s/def wire-command :- wire.in.command/Command
  (helpers.schema/generate wire.in.command/FeedPaper {:index      0
                                                      :type       :feed-paper
                                                      :scan-lines 42}))

(s/def wire-print-text-command :- wire.in.command/PrintText
  (helpers.schema/generate wire.in.command/PrintText {:content text-content
                                                      :index   0}))

(s/def wire-document :- wire.in.document/Document
  {:commands [wire-command]})

(s/def internal-command :- models.command/Command
  (helpers.schema/generate models.command/Command {:document-id document-id
                                                   :type        :feed-paper}))

(s/def internal-document :- models.document/Document
  (helpers.schema/generate models.document/Document {:id         document-id
                                                     :created-at (Date.)
                                                     :commands   [internal-command]}))

(s/def database-command :- wire.postgresql.command/Command
  (helpers.schema/generate wire.postgresql.command/FeedPaper {}))

(s/def database-print-text-command :- wire.postgresql.command/PrintText
  (helpers.schema/generate wire.postgresql.command/PrintText {:content text-content}))

(s/def database-document :- wire.postgresql.document/Document
  (helpers.schema/generate wire.postgresql.document/Document {}))

(s/def internal-print-text-command :- models.command/PrintText
  (helpers.schema/generate models.command/PrintText {:document-id document-id
                                                     :content     text-content}))
