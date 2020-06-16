#_(require '[parse-c :as pc])

#_(pc/persist-lib-res
   "src/c/sdl.clj"
   (pc/load-lib 'c.sdl "src/wut.c" "wut.o"
                {:prefix ["SDL_" "_SDL_"]
                 :kebab true
                 :libs ["SDL2"]}))  

(ns repl-core
  (:require [clojure.java.io :as io]
            [other-triple :as ot]
            [parse-c :as pc]
            [clojure.reflect :refer [reflect]]
            [clojure.pprint :refer [pp pprint]]
            
            [c.sdl :as sdl])
  (:import org.graalvm.polyglot.Context
           org.graalvm.polyglot.Source)
  (:gen-class))

(defn -main [& args]
  (println (ns-publics 'c.sdl))
  
  (c.sdl/beginning)
  (loop [quit false]
    (let [poll-res (.asInt (sdl/poll-event (sdl/get-e)))]
      (if (= 0 poll-res)
        (recur quit)
        (let [ev (.getMember (sdl/get-e) "type")
              quit (= (.asInt ev) 256)]
          (if quit
            :exit
            (recur quit))))))
  (c.sdl/end))
