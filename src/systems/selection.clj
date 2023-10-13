(ns systems.selection
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [quil.core :as q]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]))

;; TODO: move to vector ecluidian 
(defn vector2d-data?
  [data]
  (let [[x y] data]
    (if (and (number? x) (number? y))
      true false)))


(s/def ::vector vector2d-data?)

(vector2d-data? [1 2]) ; => true
(vector2d-data? (v/vector 1 2)) ; => true
(s/valid? ::vector (v/vector 1 2)); => true
(s/valid? ::vector [1 ""]); => false


(s/def ::start ::vector)
(s/def ::end ::vector)

(s/def ::box-selection (s/keys :req-un [::start ::end]))

(s/valid? ::box-selection {:start [1.0 2]
                           :end [1.0 2]})

(s/valid? ::box-selection {:start (v/vector 3  5)
                           :end [1.0 2]})


(defn- system-fn
  [state]
  (-> state
      (systems.events/handle :box-selection
                             #(do
                                (println "got" %)
                                state))))

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

