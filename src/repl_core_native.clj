(set! *warn-on-reflection* true)

(ns repl-core-native
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pp pprint]]
            #_[bindings.sdl-ni :as sdl]
            [bindings.sdl-class :as sdl]
            [native-interop :refer [*native-image* nget]])
  (:import bindings.sdl_class
           org.graalvm.polyglot.Value)
  (:gen-class))

(alter-var-root #'*native-image* (constantly true))

(defn -main [& args]
  (println "lul")
  
  (bindings.sdl_class/init (bindings.sdl_class/get-sdl-init-video))
  
  (let [window (bindings.sdl_class/create_window (bindings.sdl_class/gen_title)
                                                 0
                                                 0
                                                 640
                                                 480
                                                 (bindings.sdl_class/get-sdl-window-shown))
        screen (bindings.sdl_class/get_window_surface window)
        rect (bindings.sdl_class/create_rect 0 0 100 50)]
    
    (println "format" (.rawValue (.format screen)))
    
    (println (.type (bindings.sdl_class/get_e)))
    
    #_(bindings.sdl_class/fill_rect screen nil (bindings.sdl_class/map_rgb (.format screen) 0xFF 0xFF 0xFF))
    (bindings.sdl_class/fill_rect screen (bindings.sdl_class/get_null) ;; just nil doesn't work for ni
                                  (bindings.sdl_class/map_rgb (nget screen sdl/format) 0xFF 0xFF 0xFF))
    (bindings.sdl_class/fill_rect screen rect (bindings.sdl_class/map_rgb (nget screen sdl/format) 0xFF 0 0))
    
    (bindings.sdl_class/update_window_surface window)
    
    (println "rgb1" (bindings.sdl_class/map_rgb (.format screen) 0xFF 0xFF 0xFF))
    (println "rgb2" (bindings.sdl_class/map_rgb (.format screen) 0xFF 0 0))
    
    (loop [quit false]
      (let [quit (when-not (= 0 (bindings.sdl_class/poll_event (bindings.sdl_class/get_e)))
                   true
                   (when (= 256 (nget (sdl_class/get_e) sdl/type))
                     true))]
        (if quit
          :quit
          (recur quit))))
    
    (println "window" (.rawValue window))
    (println "screen" (.rawValue screen))
    
    (bindings.sdl_class/quit)
    
    
    
    ))
