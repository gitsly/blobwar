(ns systems.mouse
  (:require
   [quil.core :as q]
   [quil.middleware :as m]
   [ecs.ecssystem :as ecs]))


(defn- system-fn
  "Takes the entire component needed for the mouse system and updates it, returns the updated component"
  [state]
  (-> state
      ;; Note: button state needs to be tracked in core.clj for quil reasons (may be fixed somehow but unsure how)
      (assoc :x (q/mouse-x))
      (assoc :y (q/mouse-y))))

(defn- update-system
  "Point out which time component in entire game-state to update with do-time"
  [state]
  (-> state
      (update-in [:mouse] system-fn)))

(defn- draw-fn
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
    (update-system state))
  (draw [_ state]
    (draw-fn state)))

