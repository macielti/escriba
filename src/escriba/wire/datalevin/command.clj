(ns escriba.wire.datalevin.command
  (:require [schema.core :as s]))

(def command-skeleton
  {:command/id          {:db/valueType :db.type/uuid
                         :db/unique    :db.unique/identity}
   :command/index       {:db/valueType :db.type/long}
   :command/document-id {:db/valueType :db.type/uuid}
   :command/type        {:db/valueType :db.type/keyword}
   :command/orientation {:db/valueType :db.type/keyword}
   :command/size        {:db/valueType :db.type/keyword}
   :command/style       {:db/valueType :db.type/keyword}
   :command/text        {:db/valueType :db.type/string}
   :command/lines       {:db/valueType :db.type/long}})

(def types #{:command.type/print-text
             :command.type/feed-paper
             :command.type/cut})
(def Type (apply s/enum types))

(def orientations #{:command.orientation/lt
                    :command.orientation/ct
                    :command.orientation/rt})
(def Orientation (apply s/enum orientations))

(def styles #{:command.style/bold
              :command.style/italic
              :command.style/underline
              :command.style/reversed
              :command.style/normal})
(def StyleOptions (apply s/enum styles))

(def sizes #{:command.size/normal
             :command.size/double-height
             :command.size/double-width
             :command.size/double-high-wide
             :command.size/triple-high-wide
             :command.size/quadruple-high-wide})
(def SizeOptions (apply s/enum sizes))

(def command-schema
  {:command/id                           s/Uuid
   :command/index                        s/Int
   :command/document-id                  s/Uuid
   :command/type                         Type
   (s/optional-key :command/orientation) Orientation
   (s/optional-key :command/text)        s/Str
   (s/optional-key :command/lines)       s/Int
   (s/optional-key :command/size)        SizeOptions
   (s/optional-key :command/style)       StyleOptions})
(s/defschema Command command-schema)
