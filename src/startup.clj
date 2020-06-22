(ns startup
  (:require [native-interop] ;; this just sets a variable
            )
  (:gen-class))

(if native-interop/*native-image*
  (do (println "In native image context")
      (require '[bindings.sdl_ni])             ;; without this, sdl bindings won't get compiled
      (import '[bindings sdl]))
  (do (println "In polyglot context")
      (require '[bindings.sdl-ns :as sdl])))

(defn -main [& args]
  (sdl/init (sdl/get-sdl-init-video))
  
  (sdl/create-rect 0 0 100 50)
  
  (let [window (sdl/create-window (sdl/gen-title)
                                  0
                                  0
                                  640
                                  480
                                  (sdl/get-sdl-window-shown))
        screen (sdl/get-window-surface window)
        rect (sdl/create-rect 0 0 100 50)
        ]
    (sdl/fill-rect screen (sdl/get-null) ;; just nil doesn't work for ni
                   (sdl/map-rgb (.format screen) 0xFF 0xFF 0xFF))
    (sdl/fill-rect screen rect (sdl/map-rgb (.format screen) 0xFF 0 0))
    
    (sdl/update-window-surface window)
    
    (println "rgb1" (sdl/map-rgb (.format screen) 0xFF 0xFF 0xFF))
    (println "rgb2" (sdl/map-rgb (.format screen) 0xFF 0 0))
    
    (loop [quit false]
      (let [quit (when-not (= 0 (sdl/poll-event (sdl/get-e)))
                   true
                   (when (= 256 (.type (sdl/get-e)))
                     true))]
        (if quit
          :quit
          (recur quit))))
    
    (sdl/quit)))

(comment
  
  org.graalvm.polyglot.HostAccess$Implementable
  
  (clojure.core/gen-interface :name
                              ^{org.graalvm.polyglot.HostAccess$Implementable true}
                              Event3
                              :methods [[type [] int]])
  
  (import 'Event3)
  (type Event)
  (.hasMembers (sdl/get-e))
  (.type (.as (sdl/get-e) Event3))
  
  (sdl/poll-event (.as (sdl/get-e) Event3))
  
  (get (.as (sdl/get-e) java.util.Map) "type")
  (keys (.as (sdl/get-e) java.util.Map))
  (.type (sdl/get-e))
  
  (-main)
  )
