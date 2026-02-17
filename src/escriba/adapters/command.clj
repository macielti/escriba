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
  [{:keys [index width height]} :- wire.in.command/Size
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :type        :size
   :width       width
   :height      height})

(s/defmethod wire->internal :style :- models.command/Style
  [{:keys [index style]} :- wire.in.command/Style
   document-id :- s/Uuid]
  {:id          (random-uuid)
   :document-id document-id
   :index       index
   :type        :style
   :style       style})

(s/defn command->datalevin :- wire.datalevin.command/Command
  [{:keys [id index text lines type orientation width height style] :as _command} :- models.command/Command
   document-id :- s/Uuid]
  (medley/assoc-some {:command/id          id
                      :command/index       index
                      :command/type        (keyword "command.type" (name type))
                      :command/document-id document-id}
                     :command/text text
                     :command/lines lines
                     :command/orientation (some->> orientation name (keyword "command.orientation"))
                     :command/style (some->> style name (keyword "command.style"))
                     :command/width width
                     :command/height height))

(s/defn datalevin->command :- models.command/Command
  [{:command/keys [id index text lines document-id type orientation height width style]} :- wire.datalevin.command/Command]
  (medley/assoc-some {:id          id
                      :index       index
                      :type        (-> type name keyword)
                      :document-id document-id}
                     :text text
                     :lines lines
                     :orientation (some-> orientation name keyword)
                     :style (some-> style name keyword)
                     :height height
                     :width width))
