(defproject blobwar "0.2.0-SNAPSHOT"
  :description "A minimalistic game written in clojure using quil for graphics."
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [quil "3.1.0"]
                 [zprint "1.2.8"]
                 [clj-time "0.15.2"]]

  :main ^:skip-aot blobwar.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
