(ns systems.playercontrol
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events :as systems.events]))

;; TODO: make a control that stores current 'state' into an atom
;; to make debugging easier in REPL.

(defn- system-fn
  [state]

  (-> state
      (systems.events/handle :mouse-click
                             #(let [x (:x %)
                                    y (:y %)]
                                ;; Post a new event for creating a blob (if such a system would exist :)
                                (systems.events/post-event state {:id :spawn-blob :x x :y y})))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

