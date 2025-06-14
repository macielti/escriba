(ns fixtures
  (:require [clojure.test :refer :all]
            [common-test-clj.helpers.schema :as helpers.schema]
            [escriba.models.document :as models.document]
            [escriba.wire.in.command :as wire.in.command]
            [escriba.wire.in.document :as wire.in.document]
            [escriba.wire.postgresql.command :as wire.postgresql.command]
            [escriba.wire.postgresql.document :as wire.postgresql.document]
            [schema.core :as s])
  (:import (java.util Date)))

(def document-id (random-uuid))

(s/def wire-command :- wire.in.command/Command
  {:index      0
   :type       :feed-paper
   :scan-lines 42})

(s/def wire-document :- wire.in.document/Document
  {:commands [wire-command]})

(s/def internal-document :- models.document/Document
  (helpers.schema/generate models.document/Document {:created-at (Date.)}))

(s/def database-command :- wire.postgresql.command/Command
  (helpers.schema/generate wire.postgresql.command/FeedPaper {}))

(s/def database-document :- wire.postgresql.document/Document
  (helpers.schema/generate wire.postgresql.document/Document {}))
