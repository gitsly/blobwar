(ns systems.selection
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [quil.core :as q]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]))

(s/def ::start (s/coll-of number?))
(s/def ::end (s/coll-of number?))

(s/def ::box-selection (s/keys :req-un [::start ::end]))

(s/valid? ::box-selection {:start [1.0 2]
                           :end [1.0 2]})



(defn- system-fn
  [state]
  state)

(defn- draw-fn
  "Draws selection box in screen space"
  [state]
  (q/push-matrix)
  (q/reset-matrix) ; Loads the identity matrix
  (q/stroke 0 0 0 200)
  (q/fill 0 0 0 10)
  (if (contains? (-> state :mouse :button) :left)
    (do
      (let [x1 (get-in state [:mouse :pressed :x])
            y1 (get-in state [:mouse :pressed :y])
            x2 (get-in state [:mouse :x])
            y2 (get-in state [:mouse :y])
            width (- x2 x1)
            height (- y2 y1)]
        (q/rect x1 y1 width height))))
  (q/pop-matrix)
  state)


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    (draw-fn
     state)))

