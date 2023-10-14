;; Note: when using outside this file :components.common/xyz
;; or when in combination with :as
;; ::c/xyz
(ns components.common
  (:require
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]))

;; Base-specifications

(s/def ::translation ::v/vector)
(s/def ::velocity ::v/vector)
(s/def ::accel ::v/vector)
(s/def ::max-velocity number?)
(s/def ::size number?)
(s/def ::selected boolean?)


;;----------------------
;; Components that are expected in the game state 
;; (for entities to be composed of)
;;----------------------

;; Movement component
(s/def ::movement (s/keys :req-un [::velocity
                                   ::accel
                                   ::max-velocity]))

;; Is it a moving entity? it will require a movement
;; and a translation  'component'.
(s/def ::moving (s/keys :req-un [:c/movement :c/translation]))


;; Test Sample movement component
;;(s/valid? ::movement {:velocity [100 200]
;;                      :accel [1 2]
;;                      :max-velocity 1})


;; Selectable component
(s/def ::selectable (s/keys :req-un [::selected :systems.movement/translation]))
