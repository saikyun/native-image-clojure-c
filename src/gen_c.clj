(ns gen-c
  (:require [clojure.string :as str]))

(defn generate-shadowing-function
  "Takes prototype data and generates a c function declaration.
  
  ```
  (generate-shadowing-function {:ret \"int\", :sym \"SDL_Init\", :args [{:type \"Uint32\", :sym \"flags\"}]})
  ;;=> \"int  _SHADOWING_SDL_Init(Uint32  flags) {\\n  return SDL_Init(flags);\\n}\"
  ```"
  [{:keys [ret sym pointer args]}]
  (str ret
       " "
       pointer
       " _SHADOWING_"
       sym
       "(" (str/join ", " (map (fn [{:keys [type pointer sym prefixes]}]
                                 (str (str/join " " prefixes) " " type " " pointer " " sym)) args)) ") {\n"
       "  " (when (not (and (= ret "void")
                            (nil? pointer))) "return ") sym "(" (str/join ", " (map :sym args)) ");"
       "\n}"))

(comment
  (generate-shadowing-function {:ret "int", :sym "SDL_Init", :args [{:type "Uint32", :sym "flags"}]})
  ;;=> "int  _SHADOWING_SDL_Init(Uint32  flags) {\n  return SDL_Init(flags);\n}"
  )

(defn generate-c-prototype
  "Takes prototype data and generates a c function prototype."
  [{:keys [ret sym pointer args]}]
  (str ret
       " "
       pointer
       " "
       sym
       "(" (str/join ", " (map (fn [{:keys [type pointer sym prefixes]}]
                                 (str (str/join " " prefixes) " " type " " pointer " " sym)) args)) ");"))

(defn gen-c-file
  [includes fns & [{:keys [inline-c]}]]
  (let [incs (str/join "\n" (map #(str "#include \"" % "\"") includes))]
    (str (when (seq incs)
           (str "// includes\n"
                incs
                "\n"))
         (when inline-c
           (str "\n// inline-c\n"
                inline-c
                "\n"))
         (when (seq fns)
           (str "\n// fns\n"
                (str/join "\n" fns))))))

(defn gen-h-file
  [includes fn-declarations]
  (let [incs (str/join "\n"(map #(str "#include <" % ">") includes))]
    (str incs
         "\n\n"
         (str/join "\n" fn-declarations))))

