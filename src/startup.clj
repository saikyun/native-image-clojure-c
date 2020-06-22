(ns startup
  (:require [native-interop :refer [nget]])
  (:gen-class))

(if (or (some? (System/getenv "NATIVE_IMAGE"))
        (not= "false" (System/getenv "NATIVE_IMAGE")))
  (alter-var-root #'native-interop/*native-image* (constantly true))
  (alter-var-root #'native-interop/*native-image* (constantly false)))

(if native-interop/*native-image*
  (do (require '[bindings.sdl_ni])             ;; without this, sdl bindings won't get compiled
      (import '[bindings sdl_class]))
  (require '[bindings.sdl :as sdl_class]))

(defn -main [& args]
  (sdl_class/init (sdl_class/get-sdl-init-video))
  
  (let [window (sdl_class/create-window (sdl_class/gen-title)
                                        0
                                        0
                                        640
                                        480
                                        (sdl_class/get-sdl-window-shown))
        screen (sdl_class/get-window-surface window)
        rect (sdl_class/create-rect 0 0 100 50)]
    
    (sdl_class/fill-rect screen (sdl_class/get-null) ;; just nil doesn't work for ni
                         (sdl_class/map-rgb (nget screen sdl_class/format) 0xFF 0xFF 0xFF))
    (sdl_class/fill-rect screen rect (sdl_class/map-rgb (nget screen sdl_class/format) 0xFF 0 0))
    
    (sdl_class/update-window-surface window)
    
    (println "rgb1" (sdl_class/map-rgb (nget screen sdl_class/format) 0xFF 0xFF 0xFF))
    (println "rgb2" (sdl_class/map-rgb (nget screen sdl_class/format) 0xFF 0 0))
    
    (loop [quit false]
      (let [quit (when-not (= 0 (sdl_class/poll-event (sdl_class/get-e)))
                   true
                   (when (= 256 (nget (sdl_class/get-e) sdl_class/type))
                     true))]
        (if quit
          :quit
          (recur quit))))
    
    (sdl_class/quit)))
