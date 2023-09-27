(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            ;;            [ecs.ecssystem :refer :all]
            [ecs.ecssystem :as ecs]
            [drawing.quildrawing :as quildrawing]
            [drawing.dbgview :as dbgview]
            [clj-time [core :as t]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [zprint.core :as zp]))



(comment
  (let [start-time (t/now)]
    ... do lots of work ...
    (t/in-millis (t/interval start-time (t/now)))))

;;(s/explain ::ecs/system ecs/sample-system)
;;(s/explain ::ecs/system (:definition (Drawing. {:name "name1"})))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :hsb)
  (q/text-font (q/create-font "Hack" 12 true))

  {:last-time (t/now)
   :systems [(quildrawing/drawing "DrawingSys1")
             (dbgview/->Drawing "Debug text drawing system")]
   :circle-anim {:color 0
                 :angle 0 }})

(defn update-circle
[state]
{:color (mod (+ (:color state) 0.7) 255)
 :angle (+ (:angle state) 0.02) })

(defn update-state [state]
(let [now (t/now)
      dt (t/in-millis (t/interval (:last-time state) now))]
  (-> state
      (assoc :dt dt)
      (assoc :last-time now)
      (update-in  [:circle-anim] update-circle))))

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


;;(ecs/update (dbgview/->Drawing "Debug text drawing system") )

(defn update-state-via-systems 
  [state
   systems]
  (loop [systems systems
         state state]
    (if (empty? systems) 
      state
      (recur (rest systems)
             (ecs/update (first systems) state) ; Let each 'system' update the state
             ))))

(update-state-via-systems 
 {:counter 0}
 [(quildrawing/drawing "DrawingSys1")
  (dbgview/->Drawing "Debug text drawing system")])


(defn draw-state [state]
  (q/background 240)
  (q/stroke-weight 2)
  ;; TODO: make a system of text drawing.
                                        ;  (draw-text state)

  (draw-circle (:circle-anim state)))


(q/defsketch hello-quil
:title "Quil ECS testing"
:size [640 480]
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
