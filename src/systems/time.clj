(ns systems.time
  (:require
   [clj-time [core :as t]]
   [ecs.ecssystem :as ecs]))


(defn do-time
  "Takes the entire component needed for the time system and updates
  it, returns new time component"
  [state]
  (let [now (t/now)
        dt (t/in-millis (t/interval (or (:last-time state) (t/now)) now))]
    (-> state
        (assoc :dt dt)
        (assoc :last-time now))))

(defn update-time
  "Point out which time component in entire game-state to update with do-time"
  [state]
  (update-in state [:time] do-time))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (update-time state))
  (draw [_ state]
    state))

