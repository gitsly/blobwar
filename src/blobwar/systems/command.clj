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

(s/def ::commands (s/coll-of ::command :min-count 0))

;; TODO: move spec to some better location?
(s/def ::commanded (s/keys :req-un [::commands :c/translation]))


(defn command-entity
  "Fn applied to each commanded unit every update loop, currently deals only with :move type commands"
  [entity]
  (let [k-target-dist 5
        commands (:commands entity)
        cmd (first commands)
        target (:target cmd)
        translation (:translation entity)
        velocity (:velocity (:movement entity))
        zero (v/vector 0 0)
        vec-to-target (if (nil? target)
                        zero
                        (v/normalize (v/sub* target translation)))

        ;; return true if command finished
        complete-cmd (fn[entity
                         command]
                       (let [dist (v/magnitude
                                   (v/sub* translation (:target command)))]
                         (if (< dist k-target-dist)
                           true)))

        ;; evaluate if first command finished
        complete-cmds (fn [commands]
                        (if (empty? commands)
                          nil
                          (if (complete-cmd entity cmd)
                            (into (vector) (drop 1 commands))
                            commands)))

        finished? (empty? commands)]


    ;; TODO:
    ;; 1. Add check or add movecomponent to create defaults...
    ;; 2. Remove 'command' from list once target reach, make a fn for target reached condition.
    (-> entity
        ;;(update-in [:movement :velocity] #(if (nil? %)
        ;;                                    (v/vector 0 0)
        ;;                                    %))

        ;; TODO: check if in ground contact (and can control it's own movement)
        ;; e.g. other case could be hit by explosion blast and in the process
        ;; of flying away...
        (assoc :movement {:velocity (if finished?
                                      zero ; stand still if no command
                                      (if velocity
                                        velocity ; existing velocity
                                        zero)) ; default velocity

                          :accel (if finished?
                                   zero
                                   vec-to-target)

                          :max-velocity 0.5 })

        (update :commands complete-cmds))    
    ;;(println vec-to-target)

    ))


(defn on-command
  "When receiving command"
  [state
   command]
  (let [keys (apply vector (:entities command))
        add-command (fn[entity
                        command]
                      (update-in entity [:commands] #(into (vector) (conj % command))))] 
    ;; Do 'add-command' function for entities with given keys in command
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

