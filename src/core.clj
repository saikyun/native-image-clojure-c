(ns core
  (:import HelloWorld)
  (:gen-class))

(defn -main [& args]
  (System/loadLibrary "HelloWorld")
  (HelloWorld/print))
