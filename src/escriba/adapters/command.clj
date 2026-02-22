(ns escriba.adapters.command
  (:require [escriba.models.command :as models.command]
            [escriba.wire.datalevin.command :as wire.datalevin.command]
            [escriba.wire.in.command :as wire.in.command]
            [medley.core :as medley]
            [schema.core :as s]))

(defmulti wire->internal
  (fn [command _document-id] (:type command)))

(s/defmethod wire->internal :feed-paper :- models.command/FeedPaper
  [{:keys [index lines]} :- wire.in.command/FeedPaper
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :lines       lines
   :type        :feed-paper})

(s/defmethod wire->internal :print-text :- models.command/PrintText
  [{:keys [index text]} :- wire.in.command/PrintText
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :text        text
   :type        :print-text})

(s/defmethod wire->internal :cut :- models.command/Cut
  [{:keys [index]} :- wire.in.command/Cut
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :type        :cut})

(s/defmethod wire->internal :align :- models.command/Align
  [{:keys [index orientation]} :- wire.in.command/Align
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :type        :align
   :orientation orientation})

(s/defmethod wire->internal :size :- models.command/Size
  [{:keys [index size]} :- wire.in.command/Size
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :type        :size
   :size        size})

(s/defmethod wire->internal :style :- models.command/Style
  [{:keys [index style]} :- wire.in.command/Style
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :type        :style
   :style       style})

(defmulti command->datalevin
  (fn [command _document-id] (:type command)))

(s/defmethod command->datalevin :feed-paper :- wire.datalevin.command/Command
  [{:keys [index id type lines]} :- models.command/FeedPaper
   document-id :- s/Uuid]
  {:command/id          id
   :command/index       index
   :command/type        (keyword "command.type" (name type))
   :command/lines       lines
   :command/document-id document-id})

(s/defmethod command->datalevin :print-text :- wire.datalevin.command/Command
  [{:keys [index id type text]} :- models.command/PrintText
   document-id :- s/Uuid]
  {:command/id          id
   :command/index       index
   :command/type        (keyword "command.type" (name type))
   :command/text        text
   :command/document-id document-id})

(s/defmethod command->datalevin :cut :- wire.datalevin.command/Command
  [{:keys [index id type]} :- models.command/Cut
   document-id :- s/Uuid]
  {:command/id          id
   :command/index       index
   :command/type        (keyword "command.type" (name type))
   :command/document-id document-id})

(s/defmethod command->datalevin :align :- wire.datalevin.command/Command
  [{:keys [index id type orientation]} :- models.command/Align
   document-id :- s/Uuid]
  {:command/id          id
   :command/index       index
   :command/type        (keyword "command.type" (name type))
   :command/orientation (some->> orientation name (keyword "command.orientation"))
   :command/document-id document-id})

(s/defmethod command->datalevin :size :- wire.datalevin.command/Command
  [{:keys [index id type size]} :- models.command/Size
   document-id :- s/Uuid]
  {:command/id          id
   :command/index       index
   :command/type        (keyword "command.type" (name type))
   :command/size        (some->> size name (keyword "command.size"))
   :command/document-id document-id})

(s/defmethod command->datalevin :style :- wire.datalevin.command/Command
  [{:keys [index id type style]} :- models.command/Style
   document-id :- s/Uuid]
  {:command/id          id
   :command/index       index
   :command/type        (keyword "command.type" (name type))
   :command/style       (some->> style name (keyword "command.style"))
   :command/document-id document-id})

(s/defn datalevin->command :- models.command/Command
  [{:command/keys [id index text lines document-id type orientation size style]} :- wire.datalevin.command/Command]
  (medley/assoc-some {:id          id
                      :index       index
                      :type        (-> type name keyword)
                      :document-id document-id}
                     :text text
                     :lines lines
                     :orientation (some-> orientation name keyword)
                     :style (some-> style name keyword)
                     :size (some-> size name keyword)))
