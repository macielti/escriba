(ns escriba.models.command
  (:require [schema.core :as s]))

(def base
  {:id          s/Uuid
   :index       s/Int
   :document-id s/Uuid})

(def feed-paper
  (merge base
         {:type  (s/eq :feed-paper)
          :lines s/Int}))
(s/defschema FeedPaper feed-paper)

(def print-text
  (merge base
         {:type (s/eq :print-text)
          :text s/Str}))
(s/defschema PrintText print-text)

(def cut
  (merge base
         {:type (s/eq :cut)}))
(s/defschema Cut cut)

(def orientations #{:lt :ct :rt})
(def Orientation (apply s/enum orientations))

(def align
  (merge base
         {:type        (s/eq :align)
          :orientation Orientation}))
(s/defschema Align align)

(def sizes #{:normal :double-height :double-width :double-high-wide :triple-high-wide :quadruple-high-wide})
(def SizeOptions (apply s/enum sizes))

(def size
  (merge base
         {:type (s/eq :size)
          :size SizeOptions}))
(s/defschema Size size)

(def styles #{:bold :italic :underline :normal})
(def StyleOptions (apply s/enum styles))

(def style
  (merge base
         {:type  (s/eq :style)
          :style StyleOptions}))
(s/defschema Style style)

(defn- command-type [command-types]
  #(contains? (set command-types) (:type %)))

(s/defschema Command
  (s/conditional
   (command-type [:feed-paper]) FeedPaper

   (command-type [:print-text]) PrintText

   (command-type [:cut]) Cut

   (command-type [:align]) Align

   (command-type [:size]) Size

   (command-type [:style]) Style))
