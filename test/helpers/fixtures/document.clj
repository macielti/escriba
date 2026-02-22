(ns fixtures.document
  (:require [clojure.test.check.generators :as test.check.generators]
            [common-clj.time.util :as time.util]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [escriba.wire.in.command :as wire.in.command]
            [escriba.wire.in.document :as wire.in.document]
            [java-time.api :as jt]
            [schema.core :as s])
  (:import (java.time Instant)))

(def document-id (random-uuid))
(def text-content "Lorem ipsum dolor sit amet.")

(s/def wire-command :- wire.in.command/Command
  (helpers.schema/generate wire.in.command/FeedPaper {:index 0
                                                      :type  :feed-paper
                                                      :lines 42}))

(s/def wire-document :- wire.in.document/Document
  {:commands [wire-command]})

(s/def internal-command :- models.command/Command
  (helpers.schema/generate models.command/Command {:document-id document-id
                                                   :type        :feed-paper}))

(s/def internal-document :- models.document/Document
  (helpers.schema/generate models.document/Document
                           {:id         document-id
                            :created-at (time.util/instant-now)
                            :commands   [internal-command]}
                           {Instant (test.check.generators/fmap #(jt/instant %) (test.check.generators/choose 2000 2024))}))

(s/def internal-print-text-command :- models.command/PrintText
  (helpers.schema/generate models.command/PrintText {:document-id document-id
                                                     :text        text-content}))
