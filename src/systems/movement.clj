(ns systems.movement
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]
   [systems.entities]))

(s/def ::translation ::v/vector)
(s/def ::velocity ::v/vector)
(s/def ::accel ::v/vector)
(s/def ::max-velocity number?)

;; Movement component
(s/def ::movement (s/keys :req-un [::velocity
                                   ::accel
                                   ::max-velocity]))

(s/valid? ::movement {:velocity [100 200]
                      :accel [1 2]
                      :max-velocity 1})

;; Is it a moving entity? it will require a movement
;; and a translation  'component'.
(s/def ::moving (s/keys :req-un [::movement ::translation]))


(let [a (v/vector 2 3)
      b (v/vector 2 3)
      [x y] a
      res (into [] (v/add* a b))]
  {:res res
   :x x}

  )


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
      (systems.entities/apply-fn-on ::moving move-entity)))

(defrecord Sys[definition]
  ecs/EcsSystem
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

