;; https://landofquil.clojureverse.org/
(ns systems.drawing
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ecs.ecssystem :as ecs]
            [clojure.spec.alpha :as s]))

(def non-selected-color [0 0 0 128])
(def selected-color [147 235 229 128])

(s/def ::size number?)
(s/def ::selected boolean?)

(s/def ::drawable-blob
  (s/keys :req-un [::translation ::color ::size ::selected])) ;; Require unnamespaced keys

;; Test spec
(let [thing {:translation [220 110]
             :size 12
             :selected 1
             :color [128 255 0 255]
             :fighting {:weapon "TopLaser"
                        :strength 12.0 }}] 
  (if (s/valid? ::drawable-blob thing)
    thing
    (s/explain-str ::drawable-blob thing)))



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
  (q/ellipse x y size size))

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
  (let [drawable-blobs (filter #(s/valid? ::drawable-blob %)
                               (-> state :entity :entities vals))]
    (doseq [blob drawable-blobs]
      (draw-blob blob))))



(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [_ state]
    state)
  (draw [_ state]
    (draw-fn state)
    state))
