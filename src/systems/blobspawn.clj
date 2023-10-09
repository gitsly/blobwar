(ns systems.blobspawn
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events :as systems.events]))

(defn- system-fn
  [state]
  state)

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

