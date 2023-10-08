;; https://landofquil.clojureverse.org/
(ns systems.entities
  (:require 
   [ecs.ecssystem :as ecs]
   [clojure.spec.alpha :as s]))

;;(s/def ::drawable-blob
;;  (s/keys :req-un [::translation ::color ::size])) ;; Require unnamespaced keys
(defn- do-entities
  "Do handling of entities in respect to game-engine"
  [state]
  (let [next-id (count (get-in state [:entity :entities]))]
    (-> state
        (assoc-in [:entity :next-entity-id] next-id))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [_ state]
    (do-entities
     state))
  (draw [_ state]
    state))
