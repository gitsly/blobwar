(ns blobwar.core
  (:require
   [clojure.string :as str]

   [euclidean.math.vector :as v]
   [euclidean.math.matrix :as mat]

   ;; Components
   [blobwar.components.fsm]
   [blobwar.components.common]

   ;; Systems
   [blobwar.ecs.EcsSystem]
   [blobwar.systems.dbgview]
   ))

(println "core")

(defn -main
  "Main entry point"
  [& args]
  (println "TODO: lets start something"))
