(ns blobwar.systems.movement
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.entities.utils :as eu]
   [blobwar.components.common :as c]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]
   ))

(defn check-and-apply-moving-component
  [entity]
  (assoc entity :movement {:velocity (v/vector 0.02 0)
                           :accel (v/vector 0.005 0)
                           :max-velocity 1.2 }))

(defn move-entity
[entity]
(let [{{velocity :velocity
        max-velocity :max-velocity } :movement
       translation :translation } entity]
  (assoc entity :translation (v/add* translation velocity))))

(defn apply-accel
  [velocity
   accel
   max-velocity]
  (let [new-velocity (v/add* velocity accel)
        magnitude (v/magnitude new-velocity)]
    (if (< magnitude max-velocity)
      new-velocity
      (v/scale (v/normalize new-velocity) max-velocity))))
;;(apply-accel (v/vector 1 2) (v/vector 0 1.995) 3)

(defn accel-entity
  "Apply acceleration on moving entity, adheres to max-velocity"
  [entity]
  (let [{{velocity :velocity
          accel :accel
          max-velocity :max-velocity } :movement} entity]
    (update-in entity [:movement :velocity] #(apply-accel % accel max-velocity))))

(defn- update-entity
  [entity]
  (-> entity
      accel-entity
      move-entity))

(defn- system-fn
  [state]
  (-> state
      (eu/apply-fn-on ::c/moving update-entity)))

(defrecord Sys[definition]
ecs/EcsSystem
(update-sys [data state]
            (system-fn state))
(draw-sys [_ state]
          state))

