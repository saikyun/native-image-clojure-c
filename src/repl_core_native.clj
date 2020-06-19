(ns repl-core-native
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pp pprint]]
            [bindings.sdl-ni :as sdl])
  (:import bindings.sdl_class)
  (:gen-class))

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
    
    #_(bindings.sdl_class/fill_rect screen nil (bindings.sdl_class/map_rgb (.format screen) 0xFF 0xFF 0xFF))
    (bindings.sdl_class/fill_rect screen (bindings.sdl_class/get_null) ;; just nil doesn't work for ni
                                  (bindings.sdl_class/map_rgb (.format screen) 0xFF 0xFF 0xFF))
    (bindings.sdl_class/fill_rect screen rect (bindings.sdl_class/map_rgb (.format screen) 0xFF 0 0))
    
    (bindings.sdl_class/update_window_surface window)
    
    (println "rgb1" (bindings.sdl_class/map_rgb (.format screen) 0xFF 0xFF 0xFF))
    (println "rgb2" (bindings.sdl_class/map_rgb (.format screen) 0xFF 0 0))
    
    (loop [quit false]
      (let [quit (when-not (= 0 (bindings.sdl_class/poll_event (bindings.sdl_class/get_e)))
                   (when (= 256 (.type (bindings.sdl_class/get_e)))
                     true))]
        (if quit
          :quit
          (recur quit))))
    
    (println "window" (.rawValue window))
    (println "screen" (.rawValue screen))
    
    (bindings.sdl_class/quit)
    
    
    
    ))
