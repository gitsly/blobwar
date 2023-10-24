;; Utility functions for dealing with entities
(ns blobwar.entities.utils
  (:require
   [utils.core :as u]
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

;;(defn testish
;;  [col & rest]
;;  (let[f (fn[a b] (and a b)) ]
;;    (reduce f rest)))
;;(testish
;; [1 2 3]
;; (< 4 3))


(defn get-entities
  [state
   spec]
  (let [entities (-> state :entity :entities vals)]
    (filter #(s/valid? spec %) entities)))

(defn apply-fn-on
  "applies fn over set of entities if spec matches, returns new state with fn applied over all entities with matching spec"
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
;; Unify with above somehow.
(defn apply-fn-on-keys
  "Applies fn over set of entities if spec matches, returns new complete state with fn applied over matching entities"
  [state
   keys
   entity-fn]
  (let [entities (-> state :entity :entities)]
    (assoc-in state [:entity :entities]
              (into (hash-map)
                    (for [[key val] entities]
                      [key (if (u/in? keys key)
                             (entity-fn val)
                             val)])))))


(defn get-entity-kv
  "Returns key value hash-map of entities matching spec"
  [state
   spec]
  (let [entities (-> state :entity :entities)]
    (into (hash-map )
          (filter #(s/valid? spec (val %)) entities))))

;;(into (hash-map )
;;      (filter #(do 
;;                 (println (val %))
;;                 (< 1 (val %))) (hash-map :a 1 :b 2 )))


(defn add-entity
  [state
   entity]
  (let [next-id (count (get-in state [:entity :entities]))
        insert-fn (fn[entities]
                    (merge entities (hash-map next-id entity)))]
    (-> state
        (update-in [:entity :entities] insert-fn))))
