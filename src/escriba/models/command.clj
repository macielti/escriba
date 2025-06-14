(ns escriba.models.command
  (:require [schema.core :as s]))

(def base
  {:id         s/Uuid
   :index      s/Int
   :command-id s/Uuid})

(def feed-paper
  (merge base
         {:type       (s/eq :feed-paper)
          :scan-lines s/Int}))
(s/defschema FeedPaper feed-paper)

(defn- command-type [command-types]
  #(contains? (set command-types) (:type %)))

(s/defschema Command
  (s/conditional
   (command-type [:feed-paper])
   FeedPaper))
