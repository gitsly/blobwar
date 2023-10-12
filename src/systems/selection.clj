(ns systems.selection
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [quil.core :as q]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]))

(defn- system-fn
  [state]
  (comment
    (let [view-matrix (:graphics-matrix state)
          inv-matrix (if view-matrix
                       (m/invert view-matrix))]

      (systems.events/handle state :mouse-click
                             #(let [mp (v/vector (:x %) (:y %))
                                    p (m/transform inv-matrix mp)]

                                (if (= (:button %) :left)
                                  (-> state
                                      (systems.events/post-event {:id :spawn-blob :x (v/.getX p) :y (v/.getY p)}))
                                  state)))))

  state)

(defn- draw-fn
  "Draws selection box in screen space"
  [state]
  (q/push-matrix)
  (q/reset-matrix) ; Loads the identity matrix
  (q/stroke 0 0 0 200)
  (q/fill 0 0 0 10)

  (if (contains? (-> state :mouse :button) :left)
    (do
      (let [x1 (get-in state [:mouse :pressed :x])
            y1 (get-in state [:mouse :pressed :y])
            x2 (get-in state [:mouse :x])
            y2 (get-in state [:mouse :y])
            width (- x2 x1)
            height (- y2 y1)]
        (q/rect x1 y1 width height))))
  (q/pop-matrix)
  state)

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    (draw-fn
     state)))

