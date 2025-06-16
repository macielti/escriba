(ns integration.aux.components
  (:require [clojure.test :refer :all]
            [common-clj.integrant-components.config :as component.config]
            [common-clj.integrant-components.routes :as component.routes]
            [common-test-clj.component.postgresql-mock :as component.postgresql-mock]
            [escriba.diplomat.http-server :as diplomat.http-server]
            [integrant.core :as ig]
            [service-component.core :as component.service]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.tools.logging :as logging]))

(logging/use-timbre)

(def schemas ["CREATE TABLE IF NOT EXISTS documents (id UUID PRIMARY KEY, status VARCHAR(32) NOT NULL, created_at TIMESTAMP NOT NULL, retrieved_at TIMESTAMP, completed_at TIMESTAMP, failed_at TIMESTAMP);"
              "CREATE TABLE IF NOT EXISTS commands (id UUID PRIMARY KEY, index INTEGER NOT NULL, document_id UUID NOT NULL, type VARCHAR(50) NOT NULL, scan_lines INTEGER);"
              "ALTER TABLE commands ADD COLUMN content VARCHAR;"])

(def arrangement
  {::component.config/config                   {:path "resources/config.test.edn"
                                                :env  :test}
   ::component.postgresql-mock/postgresql-mock {:schemas    schemas
                                                :components {:config (ig/ref ::component.config/config)}}
   ::component.routes/routes                   {:routes diplomat.http-server/routes}
   ::component.service/service                 {:components {:postgresql (ig/ref ::component.postgresql-mock/postgresql-mock)
                                                             :config     (ig/ref ::component.config/config)
                                                             :routes     (ig/ref ::component.routes/routes)}}})

(defn start-system! []
  (timbre/set-min-level! :info)
  (ig/init arrangement))

(def -main start-system!)
