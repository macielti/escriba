(ns escriba.diplomat.job
  (:require [chime.core :as chime]
            [escriba.controllers.document :as controllers.document]
            [schema.core :as s])
  (:import (java.time Duration Instant)))

(s/defn back-to-queue!
  [{{:keys [datalevin]} :components}]
  (controllers.document/back-to-queue! datalevin))

(defn every-one-minute []
  (chime/periodic-seq (Instant/now) (Duration/ofMinutes 1)))

(def jobs
  {:pending-for-too-long {:schedule     every-one-minute
                          :interceptors []
                          :handler      back-to-queue!}})
