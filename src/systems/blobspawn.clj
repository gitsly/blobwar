(ns systems.blobspawn
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events]
   [systems.entities]))

(defn- system-fn
  [state]
  (systems.events/handle state :spawn-blob
                         #(do
                            (println "Spawn a blob via event: " %)
                            (systems.entities/add-entity
                             state {:translation [(:x %) (:y %)]
                                    :color [255 0 0 255]
                                    :size 10 }))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

