;; Handle move command (ing) of entities
;;  
(ns blobwar.systems.command
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.events :as events]
   [blobwar.entities.utils :as eu]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [blobwar.components.common :as c]
   [clojure.spec.alpha :as s]))

(s/def ::target ::v/vector) ; could it be another entity as well?

(s/def ::command (s/keys :req-un [::target]))

(s/def ::commands (s/coll-of ::command))

;; TODO: move spec to some better location?
(s/def ::commanded (s/keys :req-un [::commands :c/translation]))

(defn command-entity
  [entity]
  (let [{target :target} (-> entity :commands first)
        translation (:translation entity)
        vec-to-target nil]
    
    ;;  (let [{{velocity :velocity
    ;;          max-velocity :max-velocity } :movement
    ;;         translation :translation } entity]
    ;;    (assoc entity :translation (v/add* translation velocity)))
    ;;
    entity))

(defn on-command
  [state
   command]
  (println "Command received" command)
  state)


(let [col (hash-map 0 :a 1 :b 2 :c)
      s [0 2]]
  (for [i s]
    (get col i)))

;;(get (hash-map 0 :a 1 :b) 0)


(defn- system-fn
  [sys
   state]
  (-> state
      ;; Receieve new commands
      (events/handle :command #(on-command state %))

      ;; Update all commanded units
      (eu/apply-fn-on ::commanded command-entity)))

;; released, click -> works
;; click, released -> only drag works
;; solo -> both works separately

(defrecord Sys[definition]
ecs/EcsSystem ; Realizes the EcsSystem protocol
(update-sys [sys state]
            (system-fn sys state))
(draw-sys [_ state]
          state))

