(ns blobwar.core
  (:require
   [clojure.spec.alpha :as s]
   [clojure.string :as str]

   ;; https://landofquil.clojureverse.org/
   [quil.core :as q]
   [quil.middleware :as m]

   [clj-time [core :as t]]
   [zprint.core :as zp]

   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as mat]

   ;; Custom quil middlewares
   [blobwar.middlewares.navigation :as nav]

   ;; Components
   [blobwar.components.fsm]
   [blobwar.components.common]

   ;; Entities (functions to apply to entities with specific set of components)
   [blobwar.entities.blob]

   ;; Systems
   [blobwar.ecs.EcsSystem]
   [blobwar.systems.dbgview]
   [blobwar.systems.entities]
   [blobwar.systems.events]
   [blobwar.systems.blobspawn]
   [blobwar.systems.mouse]
   [blobwar.systems.drawing]
   [blobwar.systems.movement]
   [blobwar.systems.playercontrol]
   [blobwar.systems.selection]
   [blobwar.systems.time]
   ))

(println "core")

(defn -main
  "Main entry point"
  [& args]
  (println "TODO: lets start something"))
