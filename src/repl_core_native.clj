(ns repl-core-native
  (:require [clojure.java.io :as io]
            [parse-c :as pc]
            [clojure.pprint :refer [pp pprint]]
            [sdl-native :as sdl])
  (:gen-class))

(defn -main [& args]
  (println "lul")
  #_(println (ns-publics 'c.sdl))
  (sdl_native_lib/beginning)
  (sdl_native_lib/middle)
  #_(loop [quit false]
      (let [poll-res (.asInt (sdl/poll-event (sdl/get-e)))]
        (if (= 0 poll-res)
          (recur quit)
          (let [ev (.getMember (sdl/get-e) "type")
                quit (= (.asInt ev) 256)]
            (if quit
              :exit
              (recur quit))))))
  (sdl_native_lib/end))
