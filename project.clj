(defproject hello-quil "0.1.0-SNAPSHOT"
  :description "Sample game using Quil"
  :license {:name "GPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [quil "3.1.0"]
                 [zprint "1.2.8"]
                 [euclidean "0.2.0"]
                 [clj-time "0.15.2"]]

  :main ^:skip-aot hello-quil.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
