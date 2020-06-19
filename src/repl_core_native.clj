(ns repl-core-native
  (:require [clojure.java.io :as io]
            [clojure.pprint :refer [pp pprint]]
            [sdl-native :as sdl])
  (:gen-class))

(defn -main [& args]
  (println "lul")
  
  (sdl_native_lib/init 0 #_ (sdl/get-sdl-init-video))
  
  (comment
    
    (let [window (sdl_native_lib/create_window (sdl_native_lib/gen_title)
                                               0
                                               0
                                               640
                                               480
                                               (sdl/get-sdl-window-shown))
          screen (sdl_native_lib/get_window_surface window)
          rect (sdl_native_lib/create_rect 0 0 100 50)
          format (.format screen)]
      (println "format" (.rawValue format))
      
      #_(sdl_native_lib/fill_rect screen nil (sdl_native_lib/map_rgb (.format screen) 0xFF 0xFF 0xFF))
      (sdl_native_lib/fill_rect screen (sdl_native_lib/get_null) ;; just nil doesn't work for ni
                                (sdl_native_lib/map_rgb (.format screen) 0xFF 0xFF 0xFF))
      (sdl_native_lib/fill_rect screen rect (sdl_native_lib/map_rgb (.format screen) 0xFF 0 0))
      
      (sdl_native_lib/update_window_surface window)

      (println "rgb1" (sdl_native_lib/map_rgb (.format screen) 0xFF 0xFF 0xFF))
      (println "rgb2" (sdl_native_lib/map_rgb (.format screen) 0xFF 0 0))
      
      (loop [quit false]
        (let [quit (when-not (= 0 (sdl_native_lib/poll_event (sdl_native_lib/get_e)))
                     (when (= 256 (.type (sdl_native_lib/get_e)))
                       true))]
          (if quit
            :quit
            (recur quit))))
      
      (println "window" (.rawValue window))
      (println "screen" (.rawValue screen)))
    
    (sdl_native_lib/sdl_quit))
  
  )
