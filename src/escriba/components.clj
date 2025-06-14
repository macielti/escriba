(ns escriba.components
  (:require [common-clj.integrant-components.config :as component.config]
            [integrant.core :as ig]
            [postgresql-component.core :as component.postgresql]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging :as logging])
  (:gen-class))

(logging/use-timbre)

(def arrangement
  {::component.config/config         {:path "resources/config.edn"
                                      :env  :prod}
   ::component.postgresql/postgresql {:components {:config (ig/ref ::component.config/config)}}
   #_::component.routes/routes           #_{:routes diplomat.http-server/routes}
   #_::component.scheduler/scheduler     #_{:jobs       diplomat.job/jobs
                                            :components components}
   #_::component.service/service       #_{:components {:routes (ig/ref ::component.routes/routes)}}})

(defn start-system! []
  (timbre/set-level! :info)
  (ig/init arrangement))

(def -main start-system!)
