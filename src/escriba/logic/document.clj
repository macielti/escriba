(ns escriba.logic.document
  (:require [escriba.models.command :as models.command]
            [escriba.models.document :as models.document]
            [schema.core :as s]))

(s/defn document-with-commands :- models.document/Document
  [document :- models.document/Document
   commands :- [models.command/Command]]
  (assoc document :commands commands))
