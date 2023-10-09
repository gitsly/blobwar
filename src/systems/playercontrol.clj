(ns systems.playercontrol
  (:require
   [systems.events]
   [ecs.ecssystem :as ecs]
   [systems.events :as systems.events]))

;; TODO: make a control that stores current 'state' into an atom
;; to make debugging easier in REPL.

(defn- system-fn
  [state]


  ;;  (let [mouse-click-event (first (filter #(and (= (:id %) :mouse-click)) (systems.events/get-events state)))]
  ;;    (if (some? mouse-click-event)
  ;;      (println "pctrl: " mouse-click-event)))

  (systems.events/handle state :mouse-click
                         #(println "xctrl: " %))

  state)


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (system-fn state))
  (draw [_ state]
    state))

