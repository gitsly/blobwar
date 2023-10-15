;; https://landofquil.clojureverse.org/
(ns blobwar.systems.drawing
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [blobwar.ecs.EcsSystem :as ecs]
            [blobwar.entities.blob :as blob]
            [clojure.spec.alpha :as s]
            ))

(def non-selected-color [0 0 0 128])
(def selected-color [147 235 229 128])

(defn draw-circle
  [[x y]
   [r g b a] ; Fill
   size
   [sr sg sb sa] ; Stroke
   stroke]
  (q/fill r g b a)
  (q/stroke sr sg sb sa)
  (q/stroke-weight stroke)
  (q/ellipse-mode :center)
  (q/ellipse x y  size size))

(defn draw-blob
  [blob]
  (let [selected (:selected blob)
        stroke-color (if selected
                       selected-color
                       non-selected-color)
        stroke-width (if selected
                       4
                       0.5)]
    (draw-circle
     (:translation blob)
     (:color blob)
     (:size blob)
     stroke-color
     stroke-width)))


(defn draw-fn
  [state]
  (let [drawable-blobs (filter #(s/valid? ::blob/blob %)
                               (-> state :entity :entities vals))]
    (doseq [blob drawable-blobs]
      (draw-blob blob))))


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [_ state]
    state)
  (draw-sys [_ state]
    (draw-fn state)
    state))
