(ns core
  (:import TripletLib)
  (:gen-class))

(defn -main [& args]
  (let [t (TripletLib/allocRandomTriple)]
    (println (.getId (.subject t)))
    (TripletLib/freeTriple t)))
