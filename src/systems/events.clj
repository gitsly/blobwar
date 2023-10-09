;; https://landofquil.clojureverse.org/
(ns systems.events
  (:require 
   [ecs.ecssystem :as ecs]
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
                    ])

(def sample-state { :event {:events sample-events}})
;;(map #(s/valid? ::event %) sample-events)

(defn post-event
  "Adds the passed event to the list, checks event validity before adding"
  [state
   event]
  ;;(println "Post event: " event)
  (if (s/valid? ::event event)
    (update-in state [:event :events]
               #(conj % event))
    state))

;; TODO: make some on-event function that utilizes the get-events
;; to make it easy to do if some event occured in state processing loop

(defn get-events
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


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [_ state]
    (do-events
     state))
  (draw [_ state]
    state))
