(ns blobwar.systems.blobspawn
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.events :as events]
   [blobwar.systems.entities :as entities]
   [blobwar.entities.blob :as blob]
   [clojure.spec.alpha :as s]))

(defn- system-fn
  [state]
  ;; re-frame style?
  ;; event handlers, can be chained... but also mixed with other state updates
  (events/handle state :spawn-blob
                 #(do
                    ;;(println "Spawn a blob via event: " %)
                    (entities/add-entity
                     state (merge blob/default
                                  {:translation [(:x %) (:y %)] }))

                    )))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [data state]
    (system-fn state))
  (draw-sys [_ state]
    state))

