(ns escriba.diplomat.job
  (:require [chime.core :as chime]
            [escriba.controllers.document :as controllers.document]
            [schema.core :as s])
  (:import (java.time Duration Instant)))

(s/defn back-to-queue!
  [{{:keys [postgresql]} :components}]
  (controllers.document/back-to-queue! postgresql))

(defn every-one-minute []
  (chime/periodic-seq (Instant/now) (Duration/ofMinutes 1)))

(def jobs
  {:pending-for-too-long {:schedule     every-one-minute
                          :interceptors []
                          :handler      back-to-queue!}})
