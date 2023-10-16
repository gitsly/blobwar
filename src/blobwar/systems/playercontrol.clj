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
        ;; Check if right mouse is dragged then released (selection)
        (events/handle
         :mouse-click
         #(let [mp (v/vector (:x %) (:y %))
                p (m/transform inv-view-matrix mp)]
            (if (= (:button %) :left)
              (-> state
                  (events/post-event {:id :spawn-blob :x (v/.getX p) :y (v/.getY p)}))
              state)))

        (events/handle
         :mouse-released
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
            (if (and (not (= (:start drag) (:end drag)))
                     (= button :left)) 
              (do
                (println "Posting" drag)
                (events/post-event state drag))
              state)
            ;;(println {:event %
            ;;          :pressed pressed })
            state))
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

