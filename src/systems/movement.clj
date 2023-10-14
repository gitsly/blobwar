(ns systems.movement
  (:require
   [components.common :as c]
   [systems.events]
   [ecs.ecssystem :as ecs]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]
   [systems.entities]))

(defn move-entity
  [entity]
  (let [{{velocity :velocity
          max-velocity :max-velocity } :movement
         translation :translation } entity]
    ;;  (update entity :translation (v/add* translation velocity)))
    (assoc entity :translation (v/add* translation velocity))))

(defn- system-fn
  [state]
  (-> state
      (systems.entities/apply-fn-on ::c/moving move-entity)))

(defrecord Sys[definition]
  ecs/EcsSystem
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

