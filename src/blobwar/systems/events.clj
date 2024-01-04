(ns blobwar.systems.events
  (:require
   [blobwar.ecs.EcsSystem :as ecs]
   [clojure.spec.alpha :as s]))

(s/def ::id keyword?)

(s/def ::event
  (s/keys
   ;; Required unnamespaced keys
   :req-un [::id ; Unique ID of event
            ]
   ;; Optional keys
   :req-opt [
             ;; Human readable description of the event (for logs etc)
             ::description

             ;; Set true when event has been processsed  (one loop)
             ::processed

             ;; Optionally set true whenever someone has handled the event, Note:
             ;; many can handle the event, this is only an indication
             ;; that the event is actually seen by someone. Event
             ;; handling is performed in an undetermined but
             ;; deterministic order, dictated by the set of systems
             ;; and entities existing in the engine.
             ::handled
             ]))


(def sample-events [{:id :test1
                     :description "test event1"}
                    {:id :ev1
                     :description "processed event" :processed true }
                    {:id :ev2
                     :description "event2"}
                    {:id :spawn-blob, :x 136.0, :y 85.0}
                    ])

(def sample-state { :event {:events sample-events}})
;;(map #(s/valid? ::event %) sample-events)


;; TODO: could this be a macro?, move to some utils
;; Sample usage
;; (-> 1
;;     (if-do false inc)
;;     (if-do true #(println "fn taking in " % " returning nil"))
;;     (if-do true dec))

;; TODO: move to utils etc
(defn if-do
  "If pred evals true apply fn over state, otherwise return state.
  Also if do-fn would return nil, then return supplied state instead"
  [state
   pred ; thruthy?
   do-fn]
  (if pred
    (let [ret (do-fn state)]
      (if (nil? ret)
        state
        ret))
    state))


;; TODO: Unit test
(-> 1
    (if-do false inc)
    (if-do true #(println "fn taking in " % " returning nil"))
    (if-do false (fn[v]
                   (println "shouldn't be printed")
                   (if (< 1 v)
                     (* v 5))))

    (if-do true dec))


(defn post-event
  "Adds the passed event to the list, checks event validity before adding"
  [state
   event]
  ;;  (println "Post event: " event)
  (if (s/valid? ::event event)
    (update-in state [:event :events] #(conj % event))
    state))

(defn- get-events
  "Returns the unprocessed events, that should be considered by listeners/handlers"
  [state]
  (filter #(not (:processed %)) (-> state :event :events)))


(defn- do-events
  "Do handling of events in respect to game-engine"
  [state]
  ;;  (Thread/sleep 500) ; To make events easier to see in dbgview.
  ;;
  (let [new-events (get-events state)
        mark-as-processed-fn (fn [events]
                               (map #(assoc % :processed true) events))]
    (-> state
        (assoc-in [:event :events] (mark-as-processed-fn new-events)))))

(defn handle
  "function that utilizes the get-events
  to make it easy to do if some event occured in state processing loop"
  [state
   event-id
   handler-fn]
  (let [first-matching-event (first (filter #(and (= (:id %) event-id)) (get-events state)))]
    (if (some? first-matching-event)
      (let [new-state (handler-fn first-matching-event)]
        (if (nil? new-state)
          state
          new-state))
      state)))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [_ state]
    (do-events
     state))
  (draw-sys [_ state]
    state))
