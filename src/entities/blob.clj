(ns entities.blob
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ecs.ecssystem :as ecs]
            [clojure.spec.alpha :as s]))

;; Note: when using outside this file :entities.blob/blob

(s/def ::size number?)
(s/def ::selected boolean?)

(s/def ::selectable (s/keys :req-un [::selected ::translation]))

(s/def ::blob
  (s/and ::selectable
         (s/keys :req-un [::color ::size]))) ;; Require unnamespaced keys

;; Test spec
(let [spec ::blob
      thing {:translation [220 110]
             :size 12
             :selected true
             :color [128 255 0 255]
             :fighting {:weapon "TopLaser"
                        :strength 12.0 }}] 
  (if (s/valid? spec thing)
    thing
    (s/explain-str spec thing)))

(def default {:color [45 128 174 255] ; Color for the blobs should be all but red, reserve red color for blood spatter =)
              :selected false
              :size 10 })

