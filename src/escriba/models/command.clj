(ns escriba.models.command
  (:require [schema.core :as s]))

(def base
  {:id          s/Uuid
   :index       s/Int
   :document-id s/Uuid})

(def feed-paper
  (merge base
         {:type       (s/eq :feed-paper)
          :scan-lines s/Int}))
(s/defschema FeedPaper feed-paper)

(def print-text
  (merge base
         {:type    (s/eq :print-text)
          :content s/Str}))
(s/defschema PrintText print-text)

(defn- command-type [command-types]
  #(contains? (set command-types) (:type %)))

(s/defschema Command
  (s/conditional
   (command-type [:feed-paper])
   FeedPaper

   (command-type [:print-text])
   PrintText))
