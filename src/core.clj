(ns core
  (:require [other-triple])
  #_(:import TripletLib)
  (:import wat.cool.OOTripletLib)
  
  (:gen-class))

(defn -main [& args]
  (println "lul")
  (let [t (OOTripletLib/allocRandomTriple)]
    (println (.getId (.subject t)))
    (OOTripletLib/freeTriple t)))
