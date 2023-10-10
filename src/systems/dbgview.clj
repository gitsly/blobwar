(ns systems.dbgview
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [zprint.core :as zp]
            [ecs.ecssystem :as ecs]))

(defn- draw-text
  [state]
  (let [text-color [128 128 128]
        text-formatting-width 64
        state (select-keys state [:graphics-matrix :mouse :event]) ; filter which root keys of state to dbg
        text-content (zp/zprint-str
                      state
                      text-formatting-width)]
    ;; Note that the border (the stroke) is centered on the point where
    ;; the shape is anchored.
    (q/push-matrix)
    (q/reset-matrix) ; Loads the identity matrix
    (q/fill text-color)
    (q/text text-content
            10
            10)
    (q/pop-matrix)))


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    state)
  (draw [_ state]
    (draw-text state)
    state))

