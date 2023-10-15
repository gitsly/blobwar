(ns blobwar.ecs.EcsSystem
  (:require
   [clojure.string :as str]))

(println "blobwar.ecs.EcsSystem")

(defprotocol EcsSystem
  "A EcsSystem. Takes the entire ESC state and performs the systems
  actions upon it. Returns the updated state"
  (update-sys [this state] "Update state in system, returns new state")
  (draw-sys [this state] "System called during draw portion of engine"))
