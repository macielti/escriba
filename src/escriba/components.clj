(ns escriba.components
  (:require [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [integrant.core :as ig]
            [postgresql-component.core :as component.postgresql]
            [service-component.core :as component.service]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging :as logging])
  (:gen-class))

(logging/use-timbre)

(def arrangement
  {::component.config/config           {:path "resources/config.edn"
                                        :env  :prod}
   ::component.postgresql/postgresql   {:components {:config (ig/ref ::component.config/config)}}
   ::component.prometheus/prometheus   {:metrics []}
   ::component.http-client/http-client {:components {:config     (ig/ref ::component.config/config)
                                                     :prometheus (ig/ref ::component.prometheus/prometheus)}}
   ::component.new-relic/new-relic     {:components {:config      (ig/ref ::component.config/config)
                                                     :http-client (ig/ref ::component.http-client/http-client)}}
   #_::component.routes/routes           #_{:routes diplomat.http-server/routes}
   #_::component.scheduler/scheduler     #_{:jobs       diplomat.job/jobs
                                            :components components}
   ::component.service/service         {:components {:routes (ig/ref ::component.routes/routes)}}})

(defn start-system! []
  (timbre/set-level! :info)
  (ig/init arrangement))

(def -main start-system!)
