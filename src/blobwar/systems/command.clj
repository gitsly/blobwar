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
  "Fn applied to each commanded unit every update loop"
  [entity]
  (let [cmd (-> entity :commands first)
        {target :target} cmd
        translation (:translation entity)
        vec-to-target (v/normalize (v/sub* target translation))]

    ;; TODO:
    ;; 1. Add check or add movecomponent to create defaults...
    ;; 2. Remove 'command' from list once target reach, make a fn for target reached condition.
    (-> entity
        (update-in [:movement :velocity] #(if (nil? %)
                                            (v/vector 0 0)
                                            %))
        (update :movement #(merge % {:accel vec-to-target
                                     :max-velocity 0.5 })))    
    ;;(println vec-to-target)

    ))

(defn on-command
  "When receiving command"
  [state
   command]
  (let [keys (apply vector (:entities command))
        add-command (fn[entity
                        command]
                      (update-in entity [:commands] #(conj % command)))] 
    (eu/apply-fn-on-keys state keys #(add-command % command))))



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

