(ns ecs.ecssystem
  (:require 
   [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::system
  (s/keys :req-un [::name]))


;; Doesn't seem to be accessible from 'core'
(def sample-system {:name "test-ecs-system"})

;;(s/valid? ::system sample-system)
;;(s/explain ::system data )


(defprotocol EcsSystem
  "A EcsSystem. Takes the entire ESC state and performs the systems
  actions upon it. Returns the updated state"
  (update [this state] "Update state in system, returns new state")
  (draw [this state] "System called during draw portion of engine"))

