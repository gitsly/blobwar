(ns systems.blobspawn
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events]
   [systems.entities]
   [entities.blob]
   [clojure.spec.alpha :as s]))

(defn- system-fn
  [state]
  ;; re-frame style?
  ;; event handlers, can be chained... but also mixed with other state updates
  (systems.events/handle state :spawn-blob
                         #(do
                            ;;(println "Spawn a blob via event: " %)
                            (systems.entities/add-entity
                             state (merge entities.blob/default
                                          {:translation [(:x %) (:y %)] })))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

