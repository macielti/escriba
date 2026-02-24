(ns escriba.db.datalevin.config
  (:require [escriba.wire.datalevin.command :as wire.datalevin.command]
            [escriba.wire.datalevin.document :as wire.datalevin.document]))

(def schema
  (merge wire.datalevin.command/command-skeleton
         wire.datalevin.document/document))
