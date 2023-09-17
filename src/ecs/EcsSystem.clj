(ns ecs.ecssystem
  (:require 
   [clojure.spec.alpha :as s]))

(s/def ::name string?)

(s/def ::system
  (s/keys :req-un [::name]))


(def sample-system {:name "test-ecs-system"})

;;(s/valid? ::system data)
;;(s/explain ::system data )


(defprotocol EcsSystem
  "A EcsSystem. Takes the entire ESC state and performs the systems
  actions upon it. Returns the updated state"
  (update [this state] "Update state in system"))

