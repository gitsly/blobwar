(ns drawing.quildrawing
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [ecs.ecssystem :as ecs]
            [clj-time [core :as t]]
            [clojure.spec.alpha :as s]))


(defrecord Drawing[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update [_ state]
    state))

;; TODO: function can be generalized by passing in ::spec as well.
(defn drawing
  "Returns a Drawing system, if provided data is not valid, returns error describing structure"
  [name]
  (let [definition {:name name}
        system (Drawing. definition)
        valid? (s/valid? ::ecs/system definition)]
    (if valid?
      system
      (s/explain ::ecs/system definition))))



