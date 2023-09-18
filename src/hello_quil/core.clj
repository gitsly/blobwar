(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            ;;            [ecs.ecssystem :refer :all]
            [ecs.ecssystem :as ecs]
            [clj-time [core :as t]]
            [clojure.spec.alpha :as s]))

(comment
  (let [start-time (t/now)]
    ... do lots of work ...
    (t/in-millis (t/interval start-time (t/now))))
  )

(s/explain ::ecs/system ecs/sample-system)


(defrecord Drawing[definition]
  ecs/EcsSystem
  (update [_ state]
    state))

;; TODO: function can be generalized by passing in ::spec as well.
(defn drawing-system
  "Returns a Drawing system, if provided data is not valid, returns error describing structure"
  [name]
  (let [definition {:name name}
        system (Drawing. definition)
        valid? (s/valid? ::ecs/system definition)]
    (if valid?
      system
      (s/explain ::ecs/system definition))))

(drawing-system "test")

;;(if check
;;  system
;;  {:error (s/explain ::ecs/system system)
;;   :system system }



(s/explain ::ecs/system (:definition 
                         (Drawing. {:name "name1"})))



(defn setup []
                                        ; Set frame rate to 30 frames per second.
(q/frame-rate 30)
                                        ; Set color mode to HSB (HSV) instead of default RGB.
(q/color-mode :hsb)
                                        ; setup function returns initial state. It contains
                                        ; circle color and position.
(q/text-font (q/create-font "Hack" 28 true))

{:last-time (t/now)
 :circle-anim {:color 0
               :angle 0
               :last-time (t/now) }})

(defn update-circle
[state]
{:color (mod (+ (:color state) 0.7) 255)
 :angle (+ (:angle state) 0.02)
 :last-time (:last-time (t/now))})

(defn update-state [state]
(let [now (t/now)
      dt (t/in-millis (t/interval (:last-time state) now))]
  (update-in state [:circle-anim] update-circle)))

(defn draw-circle
[state]
(q/fill (:color state) 255 255)
                                        ; Calculate x and y coordinates of the circle.
(let [angle (:angle state)
      x (* 150 (q/cos angle))
      y (* 150 (q/sin angle))]
                                        ; Move origin point to the center of the sketch.
  (q/with-translation [(/ (q/width) 2)
                       (/ (q/height) 2)]
                                        ; Draw the circle.
    (q/ellipse x y 100 100))))

(defn draw-text
[state]

(let [text-color [128 128 128]]

  ;; Note that the border (the stroke) is centered on the point where
  ;; the shape is anchored.
  (q/fill text-color)

  (q/text "heppas"
          100
          100)))


(defn draw-state [state]
(q/background 240)
(q/stroke-weight 2)

;; TODO: make a system of text drawing.
  (draw-text state)

  (draw-circle (:circle-anim state)))


(q/defsketch hello-quil
  :title "Quil ECS testing"
  :size [500 500]
                                        ; setup function called only once, during sketch initialization.
  :setup setup
                                        ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
                                        ; This sketch uses functional-mode middleware.
                                        ; Check quil wiki for more info about middlewares and particularly
                                        ; fun-mode.
  :middleware [m/fun-mode])
