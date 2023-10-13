;; https://landofquil.clojureverse.org/
(ns systems.entities
  (:require 
   [ecs.ecssystem :as ecs]
   [clojure.spec.alpha :as s]))


(def sample {:entity {:entities (hash-map 0 {:translation [200 100]
                                             :color [85 128 174 255]
                                             :selected true 
                                             :size 10
                                             :fighting {:weapon "SubLaser"
                                                        :strength 12.0 }}

                                          1 {:translation [220 110]
                                             :color [85 72 174 255]
                                             :selected false 
                                             :size 8
                                             :fighting {:weapon "TopLaser"
                                                        :strength 12.0 }})}})

(defn apply-fn-on
  "applies fn over set of entities if spec matches, returns new entity hash-map"
  [state
   spec
   entity-fn]
  (let [entities (-> state :entity :entities)]
    (assoc-in state [:entity :entities] 
              (into (hash-map)
                    (for [[key val] entities]
                      [key (if (s/valid? spec val)
                             (entity-fn val)
                             val)])))))



(defn add-entity
  [state
   entity]
  (let [next-id (count (get-in state [:entity :entities]))
        insert-fn (fn[entities]
                    (merge entities (hash-map next-id entity)))]
    (-> state
        (update-in [:entity :entities] insert-fn))))

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
