(ns blobwar.systems.playercontrol
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.events :as events]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [zprint.core :as zp]))

;; (let [m00 1
;;       m01 2 
;;       m02 3
;;       m10 4
;;       m11 5
;;       m12 6]
;;   (m/matrix [m00 m01 m02 m10 m11 m12]))
(get-in {:heppas {:test 12}} [:heppas :test])


(def player-control-test1
  {:actors
   {:player-1
    {:height 480,
     :mouse {:button #{},
             :pressed {:button :left, :x 275, :y 219},
             :x 275,
             :y 219},
     :view (m/matrix [1.0 0.0 0.0] [0.0 1.0 0.0] [0.0 0.0 1.0]),
     :view-inv (m/matrix [1.0 0.0 0.0] [0.0 1.0 0.0] [0.0 0.0 1.0])
     :width 640}}})

(defn on-mouse-click
  [state
   actor
   event]
  (let [inv-view-matrix (:view-inv actor)
        mp (v/vector (:x event) (:y event))
        [px py] (m/transform inv-view-matrix mp)
        ev {:id :spawn-blob :x px :y py }]
    (if (= (:button event) :left)
      (events/post-event state ev))))

(defn on-mouse-dragged
  [state
   actor
   event]
  (let [inv-view-matrix (:view-inv actor)
        pressed (-> actor :mouse :pressed)
        button (-> pressed :button)
        ev {:id :box-selection 
            :start (m/transform inv-view-matrix
                                (v/vector
                                 (:x pressed)
                                 (:y pressed)))
            :end (m/transform inv-view-matrix
                              (v/vector
                               (:x event)
                               (:y event)))}]
    (if (and (not (= (:start ev) (:end ev)))
             (= button :left)) 
      (events/post-event state ev))))


(defn- system-fn
  [player
   state]
  (let [player-id (-> player :definition :id)
        actor (get-in state [:actors player-id])]
    (-> state
        (events/handle :mouse-released #(on-mouse-dragged state actor %))
        (events/handle :mouse-click #(on-mouse-click state actor %))
        )
    ))

;; released, click -> works
;; click, released -> only drag works
;; solo -> both works separately

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [player state]
    (system-fn player state))
  (draw-sys [_ state]
    state))

