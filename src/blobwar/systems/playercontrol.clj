(ns blobwar.systems.playercontrol
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.events :as events]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]))

;; (let [m00 1
;;       m01 2 
;;       m02 3
;;       m10 4
;;       m11 5
;;       m12 6]
;;   (m/matrix [m00 m01 m02 m10 m11 m12]))
(get-in {:heppas {:test 12}} [:heppas :test])

(defn- system-fn
  [player
   state]

  (let [player-id (-> player :definition :id)
        actor (get-in state [:actors player-id])
        inv-view-matrix (:view-inv actor)]
    ;;(println actor)
    (-> state

        ;;        (events/handle
        ;;         :mouse-click
        ;;         #(let [mp (v/vector (:x %) (:y %))
        ;;                [px py] (m/transform inv-view-matrix mp)
        ;;                ev {:id :spawn-blob :x px :y py }]
        ;;            (if (= (:button %) :left)
        ;;              (events/post-event state ev))))

        (events/handle
         :mouse-released ; Check if right mouse is dragged then released (selection)
         #(let [pressed (-> actor :mouse :pressed)
                button (-> pressed :button)
                ev {:id :box-selection 
                    :start (m/transform inv-view-matrix
                                        (v/vector
                                         (:x pressed)
                                         (:y pressed)))
                    :end (m/transform inv-view-matrix
                                      (v/vector
                                       (:x %)
                                       (:y %)))}]
            (if (and (not (= (:start ev) (:end ev)))
                     (= button :left)) 
              (events/post-event state ev))))
        )
    ))

(comment
  (events/handle :mouse-released
                 #(let [pressed (-> actor :mouse :pressed)
                        button (-> pressed :button)
                        drag {:id :box-selection 
                              :start (m/transform inv-view-matrix
                                                  (v/vector
                                                   (:x pressed)
                                                   (:y pressed)))
                              :end (m/transform inv-view-matrix
                                                (v/vector
                                                 (:x %)
                                                 (:y %)))}]
                    (if (and
                         (not (= (:start drag) (:end drag)))
                         (= button :left)) 
                      (events/post-event state drag)
                      state)))


  )

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [player state]
    (system-fn player state))
  (draw-sys [_ state]
    state))

