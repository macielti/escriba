(ns escriba.diplomat.http-server
  (:require [escriba.diplomat.http-server.document :as diplomat.http-server.document]
            [escriba.wire.in.document :as wire.in.document]
            [service-component.interceptors :as service.interceptors]))

(def routes [["/api/documents"
              :post [(service.interceptors/schema-body-in-interceptor wire.in.document/DocumentWrapper)
                     diplomat.http-server.document/create!]
              :route-name :create-document]

             ["/api/documents"
              :get [diplomat.http-server.document/retrieve-document-to-be-printed!]
              :route-name :retrieve-document-to-be-printed]

             ["/api/documents/:document-id/ack"
              :put [diplomat.http-server.document/acknowledge!]
              :route-name :document-ack]])
