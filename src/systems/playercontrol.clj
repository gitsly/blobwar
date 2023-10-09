(ns systems.playercontrol
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events :as systems.events]))

;; TODO: make a control that stores current 'state' into an atom
;; to make debugging easier in REPL.

(defn- system-fn
  [state]
  ;; Check for mouse button released


  (if (not (empty? (systems.events/get-events state)))
    (println "go"))

  ;;  (println 
  ;;   (filter #(and (= (:id %) :mouse-click)) (systems.events/get-events state)))
  
  ;; (println "Test: ")
  ;;  (if (not (empty? ))
  ;;   println "clicked")
  ;;)

  ;; Add check if mouse button pressed (or something)
  ;;  (systems.events/post-event :add-blob {})
  state)


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

