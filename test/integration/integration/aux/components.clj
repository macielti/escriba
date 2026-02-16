(ns integration.aux.components
  (:require [clojure.test :refer :all]
            [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [datalevin.component :as component.database]
            [escriba.db.datalevin.config :as database.config]
            [escriba.diplomat.http-server :as diplomat.http-server]
            [escriba.diplomat.job :as diplomat.job]
            [integrant.core :as ig]
            [scheduler-component.core :as component.scheduler]
            [service-component.core :as component.service]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging :as logging]))

(logging/use-timbre)

(def components
  {:config    (ig/ref ::component.config/config)
   :datalevin (ig/ref ::component.database/datalevin)})

(def arrangement
  {::component.config/config       {:path "resources/config.test.edn"
                                    :env  :test}
   ::component.database/datalevin  {:schema     database.config/schema
                                    :components (select-keys components [:config])}
   ::component.scheduler/scheduler {:jobs       diplomat.job/jobs
                                    :components components}
   ::component.routes/routes       {:routes diplomat.http-server/routes}
   ::component.service/service     {:components (merge components
                                                       {:routes (ig/ref ::component.routes/routes)})}})

(defn start-system! []
  (timbre/set-min-level! :info)
  (ig/init arrangement))

(def -main start-system!)
