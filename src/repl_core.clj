(ns repl-core
  (:require [clojure.java.io :as io]
            [clojure.reflect :refer [reflect]]
            [clojure.pprint :refer [pp pprint]]
            [native-interop :refer [nget]]
            [bindings.sdl :as sdl])
  (:import org.graalvm.polyglot.Value))

(defn -main [& args]
  (sdl/init (sdl/get-sdl-init-video))
  
  (let [window (sdl/create-window (sdl/gen-title)
                                  0
                                  0
                                  640
                                  480
                                  (sdl/get-sdl-window-shown))
        screen (sdl/get-window-surface window)
        rect (sdl/create-rect 0 0 100 50)]
    (sdl/fill-rect screen nil (sdl/map-rgb (nget screen sdl/format) 0xFF 0xFF 0xFF))
    (sdl/fill-rect screen rect (sdl/map-rgb (nget screen sdl/format) 0xFF 0 0))
    
    (sdl/update-window-surface window))
  
  (println "SDL inited")
  
  (loop [quit false]
    (let [quit (when-not (= 0 (sdl/poll-event (sdl/get-e)))
                 (when (= 256 (nget (sdl/get-e) sdl/type))
                   true))]
      (if quit
        :quit
        (recur quit))))
  
  (sdl/quit)
  (println "Quit SDL"))
