(defproject woop "0.0.1"
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :plugins [[lein-exec "0.3.7"]]
  :source-paths ["src"]
  :jvm-opts ["-Dclojure.compiler.direct-linking=true"
             "-Dclojure.spec.skip-macros=true"
             "-XstartOnFirstThread"
             "-Djava.library.path=/usr/local/lib"]
  :resources ["src" "libs"]
  #_#_:aot :all
  #_#_:main repl-core
  ;; :clean-targets ^{:protect false} ["target"]
  
  :profiles {:clojure-1.10.2-alpha1 {:dependencies [[org.clojure/clojure "1.10.2-alpha1"]]}
             :runner {:main repl-core
                      :source-paths ["src"]
                      :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                 "-Dclojure.spec.skip-macros=true"
                                 "-XstartOnFirstThread"
                                 "-Djava.library.path=/usr/local/lib"]}
             :uberjar {:global-vars {*assert* false}
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.spec.skip-macros=true"]
                       :main repl-core-native
                       :aot :all}})
