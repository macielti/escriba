(ns escriba.diplomat.http-server
  (:require [escriba.diplomat.http-server.document :as diplomat.http-server.document]))

(def routes [["/api/documents"
              :post [diplomat.http-server.document/create!]
              :route-name :create-document]

             ["/api/documents"
              :get [diplomat.http-server.document/retrieve-document-to-be-printed!]
              :route-name :retrieve-document-to-be-printed]])
