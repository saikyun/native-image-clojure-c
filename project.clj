(defproject woop "0.0.1"
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :source-paths ["src"]
  :java-source-paths ["src"]
  :profiles {:clojure-1.10.2-alpha1 {:dependencies [[org.clojure/clojure "1.10.2-alpha1"]]}
             :uberjar {:global-vars {*assert* false}
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.spec.skip-macros=true"]
                       :main core
                       :aot :all}})
