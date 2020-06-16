(ns repl-core-native
  (:require [clojure.java.io :as io]
            [parse-c :as pc]
            [clojure.pprint :refer [pp pprint]]
            #_[sdl-native :as sdl])
  (:gen-class))

(defn -main [& args]
  #_(println (ns-publics 'c.sdl))
  
  #_  (c.sdl/beginning)
  #_(c.sdl/middle)
  #_(loop [quit false]
      (let [poll-res (.asInt (sdl/poll-event (sdl/get-e)))]
        (if (= 0 poll-res)
          (recur quit)
          (let [ev (.getMember (sdl/get-e) "type")
                quit (= (.asInt ev) 256)]
            (if quit
              :exit
              (recur quit))))))
  #_(c.sdl/end))
