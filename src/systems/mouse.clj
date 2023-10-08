(ns systems.mouse
  (:require
   [quil.core :as q]
   [quil.middleware :as m]
   [ecs.ecssystem :as ecs]))


(defn- system-fn
  "Takes the entire component needed for the mouse system and updates it, returns the updated component"
  [state]
  (-> state
      (assoc :x (q/mouse-x))
      (assoc :y (q/mouse-y))))

(defn- update-system
"Point out which time component in entire game-state to update with do-time"
[state]
(-> state
    (update-in [:mouse] system-fn)))


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (update-system state))
  (draw [_ state]
    state))

