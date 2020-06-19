(ns gen-c
  (:require [clojure.string :as str]
            [clojure.java.shell :refer [sh]]))

(defn snake-case
  [s]
  (-> s
      (str/replace "-" "_")
      (str/replace "." "/")))

(defn no-subdir
  [s]
  (snake-case (str/replace s "." "$")))

(defn get-c-path
  [{:keys [src-dir lib-name]}]
  (str src-dir "/" (snake-case lib-name) ".c"))

(defn get-h-path
  [{:keys [src-dir lib-name]}]
  (str src-dir "/" (snake-case lib-name) ".h"))

(defn get-so-path
  [{:keys [lib-dir lib-name]}]
  (str lib-dir "/lib" (no-subdir lib-name) ".so"))

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

(defn compile-c
  [{:keys [c-code h-code libs] :as opts}]
  (let [c-path (get-c-path opts)
        h-path (get-h-path opts)]
    (spit c-path c-code)
    (spit h-path h-code)
    (let [sh-opts (concat [(str (System/getenv "LLVM_TOOLCHAIN") "/clang") c-path]
                          (map #(str "-l" %) libs)
                          ["-shared" "-fPIC" "-o" (get-so-path opts)])]
      (apply sh sh-opts))))

(defn persist-c
  [res]
  (let [{:keys [err]} (compile-c res)]
    (when (seq err) 
      (println "ERROR:" err)
      (println "Compilation failed:" res)
      (throw (Error. err)))))

