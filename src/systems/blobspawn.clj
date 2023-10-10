(ns systems.blobspawn
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events]
   [systems.entities]))

(defn- system-fn
  [state]
  ;; re-frame style?
  ;; event handlers, can be chained... but also mixed with other state updates
  (systems.events/handle state :spawn-blob
                         #(do
                            ;;(println "Spawn a blob via event: " %)
                            (systems.entities/add-entity
                             state {:translation [(:x %) (:y %)]
                                    :color [45 128 174 255] ; Color for the blobs should be all but red, reserve red color for blood spatter =)
                                    :size 10 }))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

