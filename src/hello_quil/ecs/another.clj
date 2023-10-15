(ns hello-quil.ecs.another
  (:require
   [clojure.string :as str]))

(println "another")

(defprotocol EcsSystem
  "A EcsSystem. Takes the entire ESC state and performs the systems
  actions upon it. Returns the updated state"
  (update [this state] "Update state in system, returns new state")
  (draw [this state] "System called during draw portion of engine"))
