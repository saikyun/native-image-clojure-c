(ns repl-core
  (:require [clojure.java.io :as io]
            [parse-c :as pc]
            [clojure.reflect :refer [reflect]]
            [clojure.pprint :refer [pp pprint]]
            [c.sdl :as sdl]))

(defn -main [& args]
  (println (ns-publics 'c.sdl))
  
  (println c.sdl/beginning c.sdl/end)
  
  (sdl/beginning)
  #_(sdl/middle)
  (loop [quit false]
    (let [poll-res (.asInt (sdl/poll-event (sdl/get-e)))]
      (if (= 0 poll-res)
        (recur quit)
        (let [ev (.getMember (c.sdl/get-e) "type")
              quit (= (.asInt ev) 256)]
          (if quit
            :exit
            (recur quit))))))
  (sdl/end))
