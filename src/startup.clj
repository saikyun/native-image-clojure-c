(ns startup
  (:require [native-interop :refer [nget]])
  (:gen-class))

(if (and (some? (System/getenv "NATIVE_IMAGE"))
         (not= "false" (System/getenv "NATIVE_IMAGE")))
  (alter-var-root #'native-interop/*native-image* (constantly true))
  (alter-var-root #'native-interop/*native-image* (constantly false)))

(if native-interop/*native-image*
  (do (println "In native image context")
      (require '[bindings.sdl_ni])             ;; without this, sdl bindings won't get compiled
      (import '[bindings sdl]))
  (do (println "In polyglot context")
      (require '[bindings.sdl-ns :as sdl])))

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
    
    (sdl/fill-rect screen (sdl/get-null) ;; just nil doesn't work for ni
                   (sdl/map-rgb (nget screen sdl/format) 0xFF 0xFF 0xFF))
    (sdl/fill-rect screen rect (sdl/map-rgb (nget screen sdl/format) 0xFF 0 0))
    
    (sdl/update-window-surface window)
    
    (println "rgb1" (sdl/map-rgb (nget screen sdl/format) 0xFF 0xFF 0xFF))
    (println "rgb2" (sdl/map-rgb (nget screen sdl/format) 0xFF 0 0))
    
    (loop [quit false]
      (let [quit (when-not (= 0 (sdl/poll-event (sdl/get-e)))
                   true
                   (when (= 256 (nget (sdl/get-e) sdl/type))
                     true))]
        (if quit
          :quit
          (recur quit))))
    
    (sdl/quit)))
