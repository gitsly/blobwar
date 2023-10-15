(ns blobwar.core
  (:require
   [clojure.string :as str]

   ;; https://landofquil.clojureverse.org/

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

   [blobwar.systems.drawing]
   ))

(println "core")

(defn -main
  "Main entry point"
  [& args]
  (println "TODO: lets start something"))
