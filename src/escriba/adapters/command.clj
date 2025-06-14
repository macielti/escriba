(ns escriba.adapters.command
  (:require [escriba.models.command :as models.command]
            [escriba.wire.in.command :as wire.in.command]
            [schema.core :as s]))

(defmulti wire->internal
  (fn [command _document-id] (:type command)))

(s/defmethod wire->internal :feed-paper :- models.command/FeedPaper
  [{:keys [index scan-lines]} :- wire.in.command/FeedPaper
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :scan-lines  scan-lines
   :type        :feed-paper})
