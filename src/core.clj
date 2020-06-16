(ns core
  (:require [other-triple :as w])
  (:import wat.cool.OOTripletLib)
  (:gen-class))

(comment
  (System/load "/Users/test/programmering/clojure/graal-native-interaction/graal/libtriple.so")
  
  )

(defn -main [& args]
  (let [t (OOTripletLib/allocRandomTriple)]
    (println "This comes from a c struct:" (.getId (.subject t)))
    (println "This too:" (.getId (.predicate t)))
    (OOTripletLib/freeTriple t)))
