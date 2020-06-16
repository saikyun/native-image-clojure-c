(defproject woop "0.0.1"
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :plugins [[lein-exec "0.3.7"]]
  :source-paths ["src"]
  ;;:java-source-paths ["src"]
  :resource-paths [#_"extra-classes" "classes" "." "/usr/local/lib"]
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"
             "-Dclojure.spec.skip-macros=true"
             "-XstartOnFirstThread"
             "-Djava.library.path=/usr/local/lib"
             "-Dclojure.server.repl={:port 5555 :accept clojure.core.server/repl}"
             ]
  :aot :all
  ;; :main repl-core
  ;; :clean-targets ^{:protect false} ["target"]
  
  :profiles {:clojure-1.10.2-alpha1 {:dependencies [[org.clojure/clojure "1.10.2-alpha1"]]}
             :uberjar {:global-vars {*assert* false}
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.spec.skip-macros=true"]
                       :main repl-core-native
                       :aot :all}})
