(ns escriba.diplomat.http-server
  (:require [escriba.diplomat.http-server.document :as diplomat.http-server.document]
            [escriba.wire.in.document :as wire.in.document]
            [io.pedestal.service.interceptors]
            [service.common :as common.service]
            [service.interceptors :as service.interceptors]))

(def routes [["/api/documents"
              :post [(service.interceptors/wire-in-body-schema wire.in.document/DocumentWrapper)
                     diplomat.http-server.document/create!]
              :route-name :create-document]

             ["/api/documents"
              :get [io.pedestal.service.interceptors/json-body
                    diplomat.http-server.document/retrieve-document-to-be-printed!]
              :route-name :retrieve-document-to-be-printed]

             ["/api/documents/:document-id/ack"
              :put [io.pedestal.service.interceptors/json-body
                    diplomat.http-server.document/acknowledge!]
              :route-name :document-ack]

             ["/api/heath"
              :get [io.pedestal.service.interceptors/json-body
                    common.service/health-check-http-request-handler]]])
