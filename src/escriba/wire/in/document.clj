(ns escriba.wire.in.document
  (:require [escriba.wire.in.command :as wire.in.command]
            [schema.core :as s]))

(s/defschema Document
  {:commands [wire.in.command/Command]})

(s/defschema DocumentWrapper
  {:document Document})
