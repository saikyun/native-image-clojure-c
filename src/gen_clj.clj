(ns gen-clj
  (:require [clojure.string :as str]))

(defn snake->kebab
  "Turns snake casing to kebab-casing -- i.e. how clojure functions look"
  [s]
  (-> s
      (str/replace #"^_" "")
      (str/replace #"(?!^)_" "-")
      (str/replace #"(?!^)([a-z][A-Z])" #(str (apply str (butlast (first %1))) "-" (second (second %1))))
      str/lower-case))

(defn remove-prefixes
  [s prefixes]
  (str/replace s (re-pattern (str/join "|" (map #(str "^" %) prefixes))) ""))

(defn gen-clojure-mapping
  "Takes proto data and creates a pair.
  The key is a symbol looking like a clojure function name.
  The value is the proto data.
  
  ```  
  (gen-clojure-mapping {:ret \"int\", :sym \"SDL_Init\", :args [{:type \"Uint32\", :sym \"flags\"}]}
                       {:prefixes [\"SDL\"]})
  ;;=> [init {:ret \"int\", :sym \"SDL_Init\", :args [{:type \"Uint32\", :sym \"flags\"}]}]
  ```
  "  
  
  [{:keys [sym] :as f} & [{:keys [prefixes kebab] :or {kebab true}}]]
  [(symbol (cond-> sym
             prefixes (remove-prefixes prefixes)
             kebab snake->kebab))
   f])

(comment
  (gen-clojure-mapping {:ret "int", :sym "SDL_Init", :args [{:type "Uint32", :sym "flags"}]}
                       {:prefixes ["SDL"]})
  ;;=> [init {:ret "int", :sym "SDL_Init", :args [{:type "Uint32", :sym "flags"}]}]
  )
