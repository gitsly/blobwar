(ns systems.playercontrol
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [systems.events :as systems.events]))

;; TODO: make a control that stores current 'state' into an atom
;; to make debugging easier in REPL.

;; (let [m00 1
;;       m01 2 
;;       m02 3
;;       m10 4
;;       m11 5
;;       m12 6]
;;   (m/matrix [m00 m01 m02 m10 m11 m12]))

(defn- system-fn
  [state]
  (let [view-matrix (:graphics-matrix state)
        inv-matrix (if view-matrix
                     (m/invert view-matrix))]

    (systems.events/handle state :mouse-click
                           #(let [mp (v/vector (:x %) (:y %))
                                  p (m/transform inv-matrix mp)]

                              (if (= (:button %) :left)
                                (-> state
                                    (assoc-in [:debug :spawntest] {:inv-matrix inv-matrix
                                                                   :mp mp
                                                                   :p p })

                                    (systems.events/post-event {:id :spawn-blob :x (v/.getX p) :y (v/.getY p)}))
                                state)))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

