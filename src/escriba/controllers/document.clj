(ns escriba.controllers.document
  (:require [escriba.models.document :as models.document]
            [schema.core :as s]))

(s/defn create!
  [document :- models.document/Document
   postgresql :- s/Any])
