(ns blobwar.systems.selection
  (:require
   [blobwar.components.common :as c]
   [blobwar.ecs.EcsSystem :as ecs]
   [blobwar.systems.entities :as entities]
   [blobwar.systems.events :as events]

   [quil.core :as q]
   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as m]
   [clojure.spec.alpha :as s]
   ))


(s/def ::start ::v/vector)
(s/def ::end ::v/vector)

(s/def ::box-selection (s/keys :req-un [::start ::end]))

(s/valid? ::box-selection {:start [1.0 2]
                           :end [1.0 2]})

(s/valid? ::box-selection {:start (v/vector 3  5)
                           :end [1.0 2]})


(defn select
  [entity
   event]
  (let [{[x y] :translation} entity
        {[x1 y1] :start 
         [x2 y2] :end } event

        ;; Make always left/top to right/bottom box
        _x1 (min x1 x2)
        _x2 (max x1 x2)
        _y1 (min y1 y2)
        _y2 (max y1 y2)

        selected (if (and (> x _x1) (< x _x2)
                          (> y _y1) (< y _y2))
                   true
                   false)]

    ;; TODO: also check ownership of selectable
    (assoc entity :selected selected)))


(defn on-box-selection
  [state
   event]
  ;;  (println "selection: " event)
  (-> state
      (entities/apply-fn-on ::c/selectable #(select % event))))

(defn- system-fn
  [state]
  (-> state
      (events/handle :box-selection
                     #(on-box-selection state %))))

(defn- draw-fn
  "Draws selection box in screen space"
  [state
   sys]
  (let [actor-id (-> sys :definition :id)
        actor (get-in state [:actors actor-id])]
    (q/push-matrix)
    (q/reset-matrix) ; Loads the identity matrix
    (q/stroke 0 0 0 200)
    (q/fill 0 0 0 10)
    (if (contains? (-> actor :mouse :button) :left)
      (do
        (let [x1 (get-in actor [:mouse :pressed :x])
              y1 (get-in actor [:mouse :pressed :y])
              x2 (get-in actor [:mouse :x])
              y2 (get-in actor [:mouse :y])
              width (- x2 x1)
              height (- y2 y1)]
          (q/rect x1 y1 width height))))
    (q/pop-matrix)
    state))


(defrecord Sys[definition]
  ecs/EcsSystem ; Realizes the EcsSystem protocol
  (update-sys [data state]
    (system-fn state))
  (draw-sys [sys state]
    (draw-fn
     state sys)))

