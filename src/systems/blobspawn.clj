(ns systems.blobspawn
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events]
   [systems.entities]))

(def default-blob
  {:color [45 128 174 255] ; Color for the blobs should be all but red, reserve red color for blood spatter =)
   :selected false
   :size 10 })

(defn- system-fn
  [state]
  ;; re-frame style?
  ;; event handlers, can be chained... but also mixed with other state updates
  (systems.events/handle state :spawn-blob
                         #(do
                            ;;(println "Spawn a blob via event: " %)
                            (systems.entities/add-entity
                             state (merge default-blob
                                          {:translation [(:x %) (:y %)] })))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

