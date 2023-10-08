;; https://landofquil.clojureverse.org/
(ns systems.events
  (:require 
   [ecs.ecssystem :as ecs]
   [clojure.spec.alpha :as s]))

(s/def ::id keyword?)

(s/def ::event
  (s/keys
   :req-un [::id] ;; Require unnamespaced keys
   ;; Optional keys
   :req-opt [::description]))

;;(s/valid? ::event {:id :some-id})

(defn- do-events
  "Do handling of events in respect to game-engine"
  [state]
  (-> state
      (assoc-in [:event :events] [{:id :ev-event }])))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [_ state]
    (do-events
     state))
  (draw [_ state]
    state))
