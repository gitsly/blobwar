;; ECS game
;; https://docs.unity3d.com/Packages/com.unity.entities@0.1/manual/ecs_core.html
(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            ;;            [ecs.ecssystem :refer :all]
            [ecs.ecssystem :as ecs]
            [drawing.quildrawing :as quildrawing]
            [clj-time [core :as t]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [euclidean.math.vector :as v]
            [zprint.core :as zp]


            ;; Custom quil middlewares
            [middlewares.navigation :as nav]

            ;; referenced systems
            [systems.dbgview :as systems.dbgview]
            [systems.time :as systems.time]
            [systems.mouse :as systems.mouse]))



;; Vector sample usage
(let [a (v/vector 2 5)
      b (v/into-vector [2 5])]
  {:normalized (v/normalize a)
   :magnitude (v/magnitude a)
   :normlen (v/magnitude (v/normalize a))
   :equal-check (= a b)})


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


  {:_INFO "Right mouse to PAN view, mouse wheel to zoom."

   :systems [(quildrawing/drawing "DrawingSys1")
             (systems.dbgview/->Sys "Debug text drawing system")
             (systems.mouse/->Sys "Mouse controller system")
             (systems.time/->Sys "Time system")]

   :circle-anim {:color 0
                 :angle 0 }})

(defn update-circle
  [state]
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.01) })


(defn do-systems 
  "Calls fn over a set of systems, Assuming EcsSystem realizing"
  [state
   systems
   fn]
  (loop [systems systems
         state state]
    (if (empty? systems) 
      state
      (recur (rest systems)
             (fn (first systems) state) ; Let each 'system' update the state
             ))))


(defn update-state [state]
  (-> state
      (do-systems  (:systems state) ecs/update)
      (update-in  [:circle-anim] update-circle) ; to get some visual representation in scene... until rendering of entities is complete
      ))


(defn draw-circle
  [state]
  (q/fill (:color state) 255 255)
                                        ; Calculate x and y coordinates of the circle.
  (let [angle (:angle state)
        x (* 150 (q/cos angle))
        y (* 150 (q/sin angle))
        sz (* 200 (q/sin angle))]
                                        ; Move origin point to the center of the sketch.

    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
                                        ; Draw the circle.
      (q/ellipse x y 100 100))

    (q/with-translation [(+ 120 (/ (q/width) 2))
                         (/ (q/height) 2)]
                                        ; Draw the circle.
      (q/ellipse x y sz 200))))


(defn draw-state [state]
  (q/background 240)
  (q/stroke-weight 2)
  ;; TODO: make a system of text drawing.
                                        ;  (draw-text state)
  ;;  (update-state-via-systems ) 
  (do-systems state (:systems state) ecs/draw)

  (draw-circle (:circle-anim state)))

(defn- mouse-dragged
  [state event]
  ;; (assoc state :mouse-dragged-info event)
  (println event))


(q/defsketch hello-quil
  :title (str "Blob" " " "War")
  :size [640 480]
                                        ; setup function called only once, during sketch initialization.
  :setup setup

  :update update-state
  :draw draw-state
  :features [:keep-on-top]

  ;; navigation-2d options. Note: this data is also passed along in the state!, nice...
  :navigation-2d {:zoom 2 ; when zoom is less than 1.0, we're zoomed out, and > 1.0 is zoomed in
                  :position [320 240]
                                        ; :mouse-buttons #{ :right :center} ;; Doesnt seem to be configurable
                  }

  :middleware [;; This sketch uses functional-mode middleware.
               ;; Check quil wiki for more info about middlewares and particularly
               ;; fun-mode.
               m/fun-mode

               ;; For zooming and mouse control
               nav/navigation
               ])


