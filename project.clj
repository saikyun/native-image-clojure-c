(defproject catamari "0.0.1"
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :plugins [[lein-exec "0.3.7"]]
  :source-paths ["src"]
  :jvm-opts []
  :resources ["src" "libs"]
  
  :profiles {:uberjar {:main catamari.examples.sdl.startup
                       :global-vars {*assert* false}
                       :uberjar-name "examples_sdl.jar"
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                  "-Dclojure.spec.skip-macros=true"
                                  "-Djava.library.path=/usr/local/lib"]
                       :aot :all}
             
             :runner {:main catamari.examples.sdl.startup
                      :source-paths ["src"]
                      :jvm-opts ["-Dclojure.compiler.direct-linking=true"
                                 "-Dclojure.spec.skip-macros=true"
                                 "-XstartOnFirstThread"
                                 "-Djava.library.path=/usr/local/lib"]}
             
             :clojure-1.10.2-alpha1 {:dependencies [[org.clojure/clojure "1.10.2-alpha1"]]}
             :socket {:jvm-opts ["-Dclojure.server.repl={:port 5555 :accept clojure.core.server/repl}"]}
             :sci {:dependencies [[babashka/babashka.nrepl "0.0.3"]
                                  [borkdude/sci "0.1.1-alpha.1"]
                                  [borkdude/sci.impl.reflector "0.0.1-java11"]]
                   :resource-paths ["/Library/Java/JavaVirtualMachines/graalvm-ce-java11-20.2.0-dev/Contents/Home/lib/svm/builder/svm.jar"]
                   :source-paths ["src-dev"]
                   :java-source-paths ["src-java"]}})
