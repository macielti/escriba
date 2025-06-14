(ns escriba.adapters.command
  (:require [escriba.models.command :as models.command]
            [escriba.wire.in.command :as wire.in.command]
            [escriba.wire.postgresql.command :as wire.postgresql.command]
            [schema.core :as s]))

(defmulti wire->internal
  (fn [command _document-id] (-> (:type command) keyword)))

(s/defmethod wire->internal :feed-paper :- models.command/FeedPaper
  [{:keys [index scan-lines]} :- wire.in.command/FeedPaper
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :scan-lines  scan-lines
   :type        :feed-paper})

(defmulti postgresql->internal
  (fn [command] (:type command)))

(s/defmethod postgresql->internal "feed-paper" :- models.command/FeedPaper
  [{:keys [id document_id index scan_lines]} :- wire.postgresql.command/Command]
  {:id          id
   :document-id document_id
   :index       index
   :scan-lines  scan_lines
   :type        :feed-paper})
