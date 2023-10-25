(ns blobwar.systems.distance
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.entities.utils :as eu]
   [blobwar.components.common :as c]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [utils.core :as uc]
   [clojure.spec.alpha :as s]))

(uc/except [{:a 1 :ex "hep"} {:a 2}]
           {:a 1})


(defn- update-entity
  [entity
   others]
  (let [;; TODO: optimize!
        get-distance (fn[a b]
                       (let[pa (:translation a)
                            pb (:translation b)]
                         (v/magnitude (v/sub* pa pb))))] 

    ;; (for [other others]
    ;;   (println (:name entity) "\t->\t" (:name other) ":\t" (get-distance entity other))

    ;;   )
    
    ;;  (eu/apply-fn-on ::c/moving update-entity)
    ;;   (println (map :id others))

    ;;    (println (:name entity) "\t->\t" (:name (first others))))

    (assoc entity :distance
           (into (hash-set) (for [other others]
                              [(:id other) (get-distance entity other)])))))

(defn- system-fn
[state]
(-> state
    (eu/apply-fn-on ::c/translated
                    #(update-entity % (eu/get-entities state ::c/translated) ))))

(defrecord Sys[definition]
ecs/EcsSystem
(update-sys [data state]
            (system-fn state))
(draw-sys [_ state]
          state))

