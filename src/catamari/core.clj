(ns catamari.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn add-prefix-to-sym
  [prefix m]
  (update m :sym #(str prefix %)))

(defn create-subdirs!
  [path]
  (doseq [d (->> (reduce
                  (fn [acc curr]
                    (conj acc (str (last acc) "/" curr)))
                  []
                  (butlast (str/split path #"/")))
                 (map #(io/file (str (System/getProperty "user.dir") "/" %))))]
    (println "Creating dir" d)
    (.mkdir d)))
