(ns blobwar.systems.time
  (:require
   [clj-time [core :as t]]
   [blobwar.ecs.EcsSystem :as ecs]))


(defn do-time
  "Takes the entire component needed for the time system and updates
  it, returns new time component"
  [state]
  (let [now (t/now)
        dt (if (:last-time state)
             (t/in-millis (t/interval (:last-time state) now))
             0)]
    (-> state
        (assoc :dt dt)
        (assoc :last-time now))))

(defn update-time
  "Point out which time component in entire game-state to update with do-time"
  [state]
  (update-in state [:time] do-time))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [data state]
    (update-time state))
  (draw-sys [_ state]
    state))

