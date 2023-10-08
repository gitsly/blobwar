;; ECS game
;; https://docs.unity3d.com/Packages/com.unity.entities@0.1/manual/ecs_core.html
(ns hello-quil.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            ;;            [ecs.ecssystem :refer :all]
            [ecs.ecssystem :as ecs]
            [clj-time [core :as t]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [euclidean.math.vector :as v]
            [zprint.core :as zp]

            ;; Custom quil middlewares
            [middlewares.navigation :as nav]

            ;; referenced systems
            [systems.drawing :as systems.drawing]
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


;;(s/explain ::ecs/system ecs/sample-system)
;;(s/explain ::ecs/system (:definition (Drawing. {:name "name1"})))


(def start-state
  {:_INFO "Right mouse to PAN view, mouse wheel to zoom. Left mouse to select"

   :systems [(systems.dbgview/->Sys "Debug text drawing system")
             (systems.mouse/->Sys "Mouse controller system")
             (systems.time/->Sys "Time system")
             (systems.drawing/->Sys "Drawing system")]

   ;;
   ;; Hash map of entities composing the game-world
   ;; each entity has  
   :entity {:next-entity-id nil
            :entities

            (hash-map 0 {:translation [200 100]
                         :color [255 0 0 255]
                         :size 10
                         :fighting {:weapon "SubLaser"
                                    :strength 12.0 }}

                      1 {:translation [220 110]
                         :color [128 255 0 255]
                         :size 8
                         :fighting {:weapon "TopLaser"
                                    :strength 12.0 }})}

   :circle-anim {:color 0
                 :angle 0 }})


(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)
  (q/text-font (q/create-font "Hack" 12 true))
  start-state)


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

;; could this be implemented as a system in the ECS domain instead... perhaps...
(defn do-entities
  "Do handling of entities in respect to game-engine"
  [state]
  (let [next-id (count (get-in state [:entity :entities]))]
    (-> state
        (assoc-in  [:entity :next-entity-id] next-id))))


(defn update-state [state]
  (-> state
      (do-systems  (:systems state) ecs/update)
      do-entities
      (update-in  [:circle-anim] update-circle) ; to get some visual representation in scene... until rendering of entities is complete
      ))


(defn draw-circle
  [state]
  (q/fill (:color state) 255 255 128)
  (q/stroke 0 0 0 128)
  (q/stroke-weight 2)
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
  ;; TODO: make a system of text drawing.
                                        ;  (draw-text state)
  ;;  (update-state-via-systems ) 
  (do-systems state (:systems state) ecs/draw)

  (draw-circle (:circle-anim state)))

(defn- mouse-dragged
  [state event]
  (assoc-in state [:mouse :dragged] event))
(defn- mouse-pressed
  [state event]
  (assoc-in state [:mouse :pressed] event))
(defn- mouse-released
  [state event]
  (assoc-in state [:mouse :released] event))

(q/defsketch hello-quil
  :title (str "Blob" " " "War")
  :size [640 480]
                                        ; setup function called only once, during sketch initialization.
  :setup setup

  :update update-state
  :draw draw-state
  :features [:keep-on-top]

  ;; Note: the mouse 'system' is fed this info 
  :mouse-pressed mouse-pressed
  :mouse-released mouse-released
  :mouse-dragged mouse-dragged

  ;; navigation-2d options. Note: this data is also passed along in the state!, nice...
  :navigation {:zoom 1 ; when zoom is less than 1.0, we're zoomed out, and > 1.0 is zoomed in
               :position [320 240]}

  :middleware [;; This sketch uses functional-mode middleware.
               ;; Check quil wiki for more info about middlewares and particularly
               ;; fun-mode.
               m/fun-mode

               ;; For zooming and mouse control
               nav/navigation
               ])
