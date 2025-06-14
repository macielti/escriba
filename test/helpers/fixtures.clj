(ns fixtures
  (:require [clojure.test :refer :all]
            [escriba.wire.in.command :as wire.in.command]
            [escriba.wire.in.document :as wire.in.document]
            [schema.core :as s]))

(def document-id (random-uuid))

(s/def wire-command :- wire.in.command/Command
  {:index      0
   :type       :feed-paper
   :scan-lines 42})

(s/def wire-document :- wire.in.document/Document
  {:commands [wire-command]})
