(ns blobwar.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [clojure.data.json :as json]

   ;; https://landofquil.clojureverse.org/
   [quil.core :as q]
   [quil.middleware :as m]

   [clj-time [core :as t]]
   [zprint.core :as zp]

   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as mat]

   ;; Custom quil middlewares
   [blobwar.middlewares.navigation :as nav]

   ;; Components
   [blobwar.components.fsm]
   [blobwar.components.common :as c]

   ;; Entities (functions to apply to entities with specific set of components)
   [blobwar.entities.blob]
   [blobwar.entities.utils :as eu]

   ;; Systems
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.dbgview]
   [blobwar.systems.command]
   [blobwar.systems.events]
   [blobwar.systems.blobspawn]
   [blobwar.systems.drawing]
   [blobwar.systems.movement]
   [blobwar.systems.playercontrol]
   [blobwar.systems.selection]
   [blobwar.systems.time]
   ))

;; TODO points
;; * Unit selection (via playercontrol)
;;   X :selected component for entities,
;;   X renderer (blob) for selected or not
;;   X spec for :selected and :translation -> ::blob
;;   X send event from playercontrol
;;     X include bounds (spec [x1 y1] [x2 y2])
;;   X selection system
;;     X check if inside bounds
;;   - Deselect (right mouse click)
;;
;; * Unit commands (via playersystem)
;; * Velocity system? (to move stuff around)
;; * Save state into JSON, load state...
;; *

(def gamestate (atom {}))
;;(println @gamestate)

;;(let [gs @gamestate
;;      ;; TODO: need Joda.Time. parser stuff...
;;      json (json/write-str gs)]
;;  (spit "c://tmp//blobstate.json" json))



;;(filter #(= (:owner %) :player-1) 
;;        (eu/get-entities @gamestate ::c/selectable))

(def start-state
  {:_INFO "Right mouse to PAN view, mouse wheel to zoom. Left mouse to select"

   :systems [
             (blobwar.systems.dbgview/->Sys "Debug text drawing system")
             (blobwar.systems.time/->Sys "Time system")
             (blobwar.systems.playercontrol/->Sys {:id :player-1 :description "Player control system"})
             (blobwar.systems.drawing/->Sys "Drawing system")
             (blobwar.systems.blobspawn/->Sys "Blob spawning system")
             (blobwar.systems.selection/->Sys {:id :player-1 :description "Selection system"})
             (blobwar.systems.movement/->Sys "Movement system")
             (blobwar.systems.command/->Sys "Command system" )

             (blobwar.systems.events/->Sys "Event system")
             ]

   ;;:mouse {   
   ;;        ;; Hash set of buttons pressed
   ;;        :button #{}}

   ;;
   ;; Hash map of entities composing the game-world
   ;; each entity has  
   :entity {:entities (hash-map 0 {:owner :player-1
                                   :translation (v/vector 200 100)
                                   :color [85 128 174 255]
                                   :selected true 
                                   :size 10 }
                                1 {:owner :player-1
                                   :translation (v/vector 20 110)
                                   :color [85 72 174 255]
                                   :selected false 
                                   :size 8}
                                )}

   :circle-anim {:color 0
                 :angle 0 }})

(defn setup
  [setup-parameters]
  (q/frame-rate 60)
  (q/color-mode :rgb)
  (q/text-font (q/create-font "Hack" 12 true))
  (merge 
   start-state
   setup-parameters))


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

(defn get-actor-data
  [nav]
  (let [root :actors
        width (q/width)
        height (q/height)
        {zoom :zoom
         pos :position } nav
        scale (mat/scale (mat/mat3) zoom)
        [px py] pos 
        trans (v/vector
               (- (/ width 2 zoom) px)
               (- (/ height 2 zoom) py))
        translation (mat/translate (mat/mat3) trans)
        view-matrix (mat/mult* scale translation)
        res {:view view-matrix
             :view-inv (mat/invert view-matrix)
             :width width
             :height height
             }]
    res))


(defn update-state
  [state]
  (reset! gamestate state)
  (-> state
      (do-systems  (:systems state) ecs/update-sys)

      (update-in [:actors (:owner state)] #(merge % (get-actor-data (:navigation state))))
      (assoc-in [:actors (:owner state) :mouse :x] (q/mouse-x))
      (assoc-in [:actors (:owner state) :mouse :y] (q/mouse-y))

      ;; to get some visual representation in scene... until rendering of entities is complete
      (update-in  [:circle-anim] update-circle)))


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
  (do-systems state (:systems state) ecs/draw-sys)
  (draw-circle (:circle-anim state)))


(defn- mouse-dragged
  [state event]
  ;;  (assoc-in state [:mouse :dragged] event)
  state)

(defn- mouse-pressed
  [state event]
  (let [button-id (q/mouse-button)
        path [:actors (:owner state) :mouse]] 
    ;;(println "pressed: " path)
    (-> state
        (assoc-in (conj path :pressed) event)
        (update-in (conj path :button) #(into (hash-set) (conj % button-id))))))


(defn- mouse-clicked
  "Fires an event in the event system"
  [state event]
  (let [ev (merge {:id :mouse-click} event)]
    (blobwar.systems.events/post-event state ev)))

(defn- mouse-released
  [state event]
  (let [button-id (q/mouse-button)
        path [:actors (:owner state) :mouse :button]
        ev (merge {:id :mouse-released} event)] 
    ;;(println "released: " path)
    (-> state
        (blobwar.systems.events/post-event ev)      
        (update-in path #(into (hash-set) (disj % button-id))))))

(defn create-sketch
  [title
   setup-parameters]
  (q/sketch  
   :size [640 480]
   :title title
   :renderer :java2d; :opengl ; 
                                        ; setup function called only once, during sketch initialization.
   :setup (fn [] (setup setup-parameters))

   :update (fn [state] (update-state state))
   :draw (fn[state] (draw-state state))
   :features [:keep-on-top]

   ;; Note: the mouse 'system' is fed this info 
   :mouse-pressed mouse-pressed
   :mouse-released mouse-released
   :mouse-dragged mouse-dragged
   :mouse-clicked mouse-clicked

   ;; navigation options. Note: this data is also passed along in the state!, nice...
   :navigation {:zoom 1 ; when zoom is less than 1.0, we're zoomed out, and > 1.0 is zoomed in
                :position [320 240]}

   :middleware [;; This sketch uses functional-mode middleware.
                ;; Check quil wiki for more info about middlewares and particularly
                ;; fun-mode.
                m/fun-mode

                ;; For zooming and mouse control
                nav/navigation
                ]))


;; update navigation middleware to get matrix, or calculate it

(defn -main
  "Main entry point"
  [& args]
  (println "In the absence of parantheses, chaos prevails")
  (create-sketch "Blob war" {:owner :player-1 }))

;; Uncomment below and execute to open a new sketch in the CIDER REPL
;;(create-sketch "Blob war" {:owner :player-1 })

;; Haze command
;;  Startup: /usr/bin/lein update-in :dependencies conj \[nrepl/nrepl\ \"1.0.0\"\] -- update-in :dependencies conj \[refactor-nrepl/refactor-nrepl\ \"3.9.0\"\] -- update-in :plugins conj \[refactor-nrepl/refactor-nrepl\ \"3.9.0\"\] -- update-in :plugins conj \[cider/cider-nrepl\ \"0.40.0\"\] -- repl :headless :host localhost

;; OF-WL4775 (works better), Java 1.8
;; refactor-nrepl 3.6.0
;; cider-nrepl 0.29.0

;; Was able to reproduce the exact same issue with the later cider versions
;; on the windows machine as well
