(ns blobwar.systems.dbgview
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [zprint.core :as zp]
            [blobwar.ecs.EcsSystem :as ecs]))

(defn- draw-text
  [state]
  (let [paths [:mouse :owner :navigation :actors]
        text-color [128 128 128]
        text-formatting-width 54
        ;; Use below str for quickly debugging specifics in state
        quick-dbg-str (zp/zprint-str
                       (:debug state)
                       text-formatting-width)
        state (select-keys state paths) ; filter which root keys of state to dbg
        text-content (zp/zprint-str
                      state
                      text-formatting-width)
        ]
    ;; Note that the border (the stroke) is centered on the point where
    ;; the shape is anchored.
    (q/push-matrix)
    (q/reset-matrix) ; Loads the identity matrix
    (q/fill text-color)
    (q/text text-content
            0
            10)

    (q/fill [255 0 0])
    (q/text quick-dbg-str 10 240)
    (q/pop-matrix)))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [data state]
    state)
  (draw-sys [_ state]
    (draw-text state)
    state))

