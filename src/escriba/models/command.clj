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

(defn- command-type [command-types]
  #(contains? (set command-types) (:type %)))

(s/defschema Command
  (s/conditional
   (command-type [:feed-paper]) FeedPaper
   (command-type [:print-text]) PrintText
   (command-type [:cut]) Cut))
