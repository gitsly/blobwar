(ns systems.playercontrol
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]))

;; (let [m00 1
;;       m01 2 
;;       m02 3
;;       m10 4
;;       m11 5
;;       m12 6]
;;   (m/matrix [m00 m01 m02 m10 m11 m12]))

(defn- system-fn
  [player
   state]
  (let [view-matrix (:graphics-matrix state)
        inv-matrix (if view-matrix
                     (m/invert view-matrix))]
    (-> state
        (systems.events/handle :mouse-released
                               #(let [pressed (-> state :mouse :pressed)
                                      drag {:id :box-selection 
                                            :start (v/vector
                                                    (:x pressed)
                                                    (:y pressed))
                                            :end (v/vector
                                                  (:x %)
                                                  (:y %))}]
                                  (if (not (= (:start drag) (:end drag))) 
                                    (systems.events/post-event state drag)
                                    state)))

        (systems.events/handle :mouse-click
                               #(let [mp (v/vector (:x %) (:y %))
                                      p (m/transform inv-matrix mp)
                                      player-id (-> player :definition :id)]

                                  (if (= (:button %) :left)
                                    (-> state
                                        (assoc-in [:debug] {:info "Playerinfo"
                                                            :player player-id
                                                            :time (str (-> state :time :last-time))})
                                        (systems.events/post-event {:id :spawn-blob :x (v/.getX p) :y (v/.getY p)}))
                                    state))))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [player state]
    (system-fn player state))
  (draw [_ state]
    state))

