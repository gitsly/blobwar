(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clj-time [core :as t]]))

(comment
  (let [start-time (t/now)]
    ... do lots of work ...
    (t/in-millis (t/interval start-time (t/now))))
  )




(defn setup []
                                        ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
                                        ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
                                        ; setup function returns initial state. It contains
                                        ; circle color and position.
  {:circle-anim {:color 0
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


(defn draw-state [state]
  (q/background 240)
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
