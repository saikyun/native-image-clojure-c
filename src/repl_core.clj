(ns repl-core
  (:require [clojure.java.io :as io]
            [clojure.reflect :refer [reflect]]
            [clojure.pprint :refer [pp pprint]]
            [c.sdl :as sdl]))

(defn -main [& args]
  (println (ns-publics 'c.sdl))
  
  (sdl/init (sdl/get-sdl-init-video))
  
  (let [window (sdl/create-window (sdl/gen-title)
                                  0
                                  0
                                  640
                                  480
                                  (sdl/get-sdl-window-shown))
        screen (sdl/get-window-surface window)
        rect (sdl/create-rect 0 0 100 50)]
    (sdl/fill-rect screen nil (sdl/map-rgb (.getMember screen "format") 0xFF 0xFF 0xFF))
    (sdl/fill-rect screen rect (sdl/map-rgb (.getMember screen "format") 0xFF 0 0))
    
    (sdl/update-window-surface window))
  
  (println "SDL inited")
  
  (loop [quit false]
    (let [quit (when-not (= 0 (sdl/poll-event (sdl/get-e)))
                 (when (= 256 (.asInt (.getMember (sdl/get-e) "type")))
                   true))]
      (if quit
        :quit
        (recur quit))))
  
  (sdl/quit)
  (println "Quit SDL")
  
  #_(sdl/beginning)
  #_(sdl/middle)
  #_(loop [quit false]
      (let [poll-res (.asInt (sdl/poll-event (sdl/get-e)))]
        (if (= 0 poll-res)
          (recur quit)
          (let [ev (.getMember (c.sdl/get-e) "type")
                quit (= (.asInt ev) 256)]
            (if quit
              :exit
              (recur quit))))))
  #_(sdl/end))
