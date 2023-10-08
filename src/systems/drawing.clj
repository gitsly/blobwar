;; https://landofquil.clojureverse.org/
(ns systems.drawing
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ecs.ecssystem :as ecs]
            [clojure.spec.alpha :as s]))

(s/def ::drawable-blob
  (s/keys :req-un [::translation ::color ::size])) ;; Require unnamespaced keys

;;(s/valid? ::drawable-blob 
;;          {:translation [220 110]
;;           :color [128 255 0 255]
;;           :fighting {:weapon "TopLaser"
;;                      :strength 12.0 }})



(defn draw-circle
  [[x y]
   [r g b a]
   size]
  (q/fill r g b a)
  (q/stroke 0 0 0 128)
  (q/stroke-weight 2)
  (q/ellipse-mode :center)

  (q/ellipse x y size size))

(defn draw-fn
  [state]
  (let [drawable-blobs (filter #(s/valid? ::drawable-blob %)
                               (-> state :entity :entities vals))]
    (doseq [blob drawable-blobs]
      (draw-circle
       (:translation blob)
       (:color blob)
       (:size blob)))))

(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [_ state]
    state)
  (draw [_ state]
    (draw-fn state)
    state))


;; TODO: function can be generalized by passing in ::spec as well.
;;(defn drawing
;;  "Returns a Drawing system, if provided data is not valid, returns error describing structure"
;;  [name]
;;  (let [definition {:name name}
;;        system (Drawing. definition)
;;        valid? (s/valid? ::ecs/system definition)]
;;    (if valid?
;;      system
;;      (s/explain ::ecs/system definition))))



