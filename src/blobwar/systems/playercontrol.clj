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

(contains? [1 2] 1)

(defn- system-fn
  [player
   state]
  (let [view-matrix (:graphics-matrix state)
        inv-matrix (if view-matrix
                     (m/invert view-matrix))]
    (-> state
        ;; Check if right mouse is dragged then released (selection)
        (events/handle :mouse-released
                               #(let [pressed (-> state :mouse :pressed)
                                      button (-> pressed :button)
                                      drag {:id :box-selection 
                                            :start (m/transform inv-matrix
                                                                (v/vector
                                                                 (:x pressed)
                                                                 (:y pressed)))
                                            :end (m/transform inv-matrix
                                                              (v/vector
                                                               (:x %)
                                                               (:y %)))}]
                                  (if (and
                                       (not (= (:start drag) (:end drag)))
                                       (= button :left)) 
                                    (events/post-event state drag)
                                    state)))

        (events/handle :mouse-click
                               #(let [mp (v/vector (:x %) (:y %))
                                      p (m/transform inv-matrix mp)
                                      player-id (-> player :definition :id)]

                                  (if (= (:button %) :left)
                                    (-> state
                                        (assoc-in [:debug] {:info "Playerinfo"
                                                            :player player-id
                                                            :time (str (-> state :time :last-time))})
                                        (events/post-event {:id :spawn-blob :x (v/.getX p) :y (v/.getY p)}))
                                    state))))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [player state]
    (system-fn player state))
  (draw-sys [_ state]
    state))

