(ns escriba.components
  (:require [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [escriba.diplomat.http-server :as diplomat.http-server]
            [escriba.diplomat.job :as diplomat.job]
            [integrant.core :as ig]
            [postgresql-component.core :as component.postgresql]
            [scheduler-component.core :as component.scheduler]
            [service-component.core :as component.service]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging :as logging])
  (:gen-class))

(logging/use-timbre)

(def components
  {:postgresql (ig/ref ::component.postgresql/postgresql)
   :config     (ig/ref ::component.config/config)})

(def arrangement
  {::component.config/config         {:path "resources/config.edn"
                                      :env  :prod}
   ::component.postgresql/postgresql {:components {:config (ig/ref ::component.config/config)}}
   ::component.routes/routes         {:routes diplomat.http-server/routes}
   ::component.scheduler/scheduler   {:jobs       diplomat.job/jobs
                                      :components components}
   ::component.service/service       {:components (merge components
                                                         {:routes (ig/ref ::component.routes/routes)})}})

(defn start-system! []
  (timbre/set-min-level! :info)
  (ig/init arrangement))

(def -main start-system!)
