(ns drawing.dbgview
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [zprint.core :as zp]
            [ecs.ecssystem :as ecs]))

(defn- draw-text
  [state]
  (let [text-color [128 128 128]
        text-formatting-width 32
        text-content (zp/zprint-str state text-formatting-width)]
    ;; Note that the border (the stroke) is centered on the point where
    ;; the shape is anchored.
    (q/fill text-color)
    (q/text text-content
            10
            10)))


(defrecord Drawing[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [data state]
    (println (:definition data))
    ;;(draw-text state)
    state))

