                                        ; BDD = Behavior
                                        ; Inpiration from here: https://github.com/unclebob/spacewar/blob/master/src/spacewar/game_logic/klingons.cljc
                                        ; And here: https://blog.cleancoder.com/uncle-bob/2018/06/06/PickledState.html
(ns components.fsm
  (:require [clojure.spec.alpha :as s]))

;; Gherkin (testing language?), GWT
;; GIVEN that we are in state S1
;; WHEN we recieve event E1
;; THEN we transition to state S2

;; "Unit" testing AAA isomorphic with GWT
;; GIVEN that we have ARRANGED the system for the test.
;; WHEN we perform the tested ACTION.
;; THEN we can ASSERT the conditions that pass the test.

;; (It seems like all events need to be defined, to make code KISS)
;;          |
;;          V
;; state [event -> newstate]
(def blob-fsm {:patrol {
                        ;; GIVEN that we are in :patrol state,
                        ;; WHEN  receive :move-command,
                        ;; THEN  transition to state :move
                        :move-command :move 
                        
                        :stopped :patrol}
               
               ;; Blob has a move command
               :move   {:move-command :move ; continue moving
                        :stopped :patrol}   ; stop

               :attack {:move-command :move
                        :stopped :attack}
               })

;; Above state-machine definition is not very satisfactory
;; suspecting the 'events' are not the ones we're looking for
;; Event for the klingon FSM is more like reffereing the the status (state)
;; of the klingon itself, nothing happening in the outside 'world'

(def klingon-fsm {:patrol {:low-antimatter :refuel
                           :low-torpedo :patrol
                           :capable :guard
                           :well-supplied :mission}
                  :refuel {:low-antimatter :refuel
                           :low-torpedo :patrol
                           :capable :guard
                           :well-supplied :mission}
                  :guard {:low-antimatter :refuel
                          :low-torpedo :guard
                          :capable :guard
                          :well-supplied :mission}
                  :mission {:low-antimatter :refuel
                            :low-torpedo :guard
                            :capable :refuel
                            :well-supplied :mission}})

(defn cruise-transition [{:keys [antimatter torpedos]}]
  "Determines the transition to take in the FSM given the state of the 'klingon'"
  (let [antimatter 20
        torpedos 10]
    (cond
      (<= antimatter 40) :low-antimatter
      (<= torpedos 40) :low-torpedo
      (and (> antimatter 40) (> torpedos 60)) :well-supplied
      :else :capable)))

(defn- change-cruise-state [klingon]
  "Perform the transition"
  (let [antimatter (:antimatter klingon)
        transition (cruise-transition klingon)
        cruise-state (:cruise-state klingon)
        new-state (if (and (= :refuel cruise-state)
                           (< antimatter 40))
                    :refuel
                    (do
                      (println "Input to FSM: state:" cruise-state ", transition(event):" transition)
                      (-> klingon-fsm cruise-state transition)))]
    (assoc klingon :cruise-state new-state)))

;; Test above
(:cruise-state
 (change-cruise-state {:antimatter 6000
                       :torpedos 2
                       :cruise-state :guard }))

;;
;; Theory: Let game events alter state (data) of entity, then 'constant' deterministic evaluation functions
;; 
;; Use-case1:
;; Player --> Blob : EVENT (give command)
;; Blob --> Blob : FSM(has-move-command)->transition

(def blob-fsm {:patrol {
                        ;; GIVEN that we are in :patrol state,
                        ;; WHEN  receive :move-command,
                        ;; THEN  transition to state :move
                        :commanded :move 
                        :stopped :patrol
                        :broken :flee
                        :rallied :patrol }
               
               :flee   {:commanded :flee
                        :stopped :flee
                        :broken :flee
                        :rallied :patrol }
               ;; Blob has a move command
               :move   {:commanded :move ; continue moving
                        :stopped :patrol
                        :broken :flee
                        :rallied :move }

               :attack {:commanded :move
                        :stopped :attack
                        :broken :flee
                        :rallied :attack }
               })

(def transitions
  (into (hash-set)  
        (flatten (map keys (-> blob-fsm vals)))))
