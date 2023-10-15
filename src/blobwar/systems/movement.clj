(ns blobwar.systems.movement
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.entities :as entities]
   [blobwar.components.common :as c]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]
   ))

(defn move-entity
  [entity]
  (let [{{velocity :velocity
          max-velocity :max-velocity } :movement
         translation :translation } entity]
    (assoc entity :translation (v/add* translation velocity))))

(defn- system-fn
  [state]
  (-> state
      (entities/apply-fn-on ::c/moving move-entity)))

(defrecord Sys[definition]
  ecs/EcsSystem
  (update-sys [data state]
    (system-fn state))
  (draw-sys [_ state]
    state))

