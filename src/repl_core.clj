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
  (c.sdl/middle)
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
