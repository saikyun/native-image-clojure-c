;; 1. take a c prototype
;; 2. generate data as below
;; 3. generate c shadowing function
;; 4. generate clojure polyglot code
;; 5. generate clojure ni code

(ns spec-c-lib
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pp pprint]]
            [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io]))


(def arg-s (str "\\s*"
                "(const\\s*)*" ;; prefixes
                "\\s*"
                "(\\w+)"       ;; type
                "((\\*|\\s)+)" ;; space / pointer stars
                "(\\w+)"       ;; symbol
                "\\s*"))

(def prototype-regex
  (re-pattern (str "(;|^)\\s*"     ;; whitespace
                   "(\\w+)"        ;; return type
                   "((\\*|\\s)+)"  ;; space / pointer stars
                   "(\\w+)"        ;; function name
                   "\\(*"          ;; beginning of args
                   "("
                   ,    arg-s      ;; first arg
                   "(," arg-s ")*" ;; more args
                   ")?"
                   "\\s*\\)"       ;; end of args
                   "\\s*(\\{|;|$)" ;; braces and stuff
                   )))

(def arg-regex (re-pattern arg-s))

(defn parse-args
  "Takes a string of c function arguments declaration
  I.e. the code between the parenthesises when declaring a c function.

  ```
  (parse-args \"int a, char* b\")
  ;;=> [{:type \"int\", :sym \"a\"} {:type \"char\", :sym \"b\", :pointer \"*\"}]
  ```"
  [args]
  (when args
    (let [args (str/split args #",")]
      (->> args
           (map #(let [[_ prefix type pointers _ sym] (re-find arg-regex %)
                       prefixes (when prefix (str/split prefix #" "))
                       pointer (when-let [s (seq (str/trim pointers))] (apply str s))]
                   (cond->
                       {:type type
                        :sym sym}
                     prefix (merge {:prefixes prefixes})
                     pointer (merge {:pointer pointer}))))
           (into [])))))

(comment
  
  )

(defn parse-c-prototype
  "Takes a c-prototype and returns prototype data.
  
  ```
  (parse-c-prototype \"int SDL_Init(Uint32 flags)\")
  ;;=> {:ret \"int\", :sym \"SDL_Init\", :args [{:type \"Uint32\", :sym \"flags\"}]}
  ```"
  [s]
  (try
    (let [[_ _ type pointers _ f-name args :as res] (re-find prototype-regex s)
          args (parse-args args)
          pointer (when-let [s (seq (str/trim pointers))] (apply str s))]
      (cond-> {:ret type 
               :sym f-name}
        args (merge {:args args})
        pointer (merge {:pointer pointer})))
    (catch Exception e
      (println "Failed parsing" (pr-str s))
      (throw e))))

(comment
  (parse-c-prototype "int SDL_Init(Uint32 flags)")
  ;;=> {:ret "int", :sym "SDL_Init", :args [{:type "Uint32", :sym "flags"}]}
  )

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
       "(" (str/join ", " (map (fn [{:keys [type pointer sym]}] (str type " " pointer " " sym)) args)) ") {\n"
       "  " (when (not= ret "void") "return ") sym "(" (str/join ", " (map :sym args)) ");"
       "\n}"))

(comment
  (generate-shadowing-function {:ret "int", :sym "SDL_Init", :args [{:type "Uint32", :sym "flags"}]})
  ;;=> "int  _SHADOWING_SDL_Init(Uint32  flags) {\n  return SDL_Init(flags);\n}"
  )

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



(defn gen-defn
  "Takes kv pair, where k is a clojure symbol and v is proto data.
  Generates `defn`-calls.
  Needs `{:lib-sym ...}` as second argument.
  This should be a symbol declared above the defn-call, which contains a polyglot library.
  
  ```
  (-> (gen-clojure-mapping {:ret \"int\", :sym \"SDL_Init\", :args [{:type \"Uint32\", :sym \"flags\"}]}
                           {:prefixes [\"SDL\"]})
      (gen-defn {:lib-sym 'sdl-sym}))
   [(def init1687 (.getMember sdl-sym \"SDL_Init\"))
    (clojure.core/defn init ([flags] (.execute init1687 (clojure.core/object-array [flags]))))]
  ```"
  [[f-sym {:keys [ret sym args]}] {:keys [lib-sym]}]
  (let [f (if (= ret "void")
            '.executeVoid
            '.execute)
        f-gensym (gensym f-sym)]
    `[(def ~(with-meta f-gensym {:private true}) (.getMember ~lib-sym ~sym))
      (defn ~f-sym
        ~(if-let [as (seq (map (comp symbol :sym) args))]
           `(~(into [] as)
             (~f ~f-gensym (object-array ~(into [] as))))
           `([]
             (~f ~f-gensym ~'empty-array))))]))

(comment
  (-> (gen-clojure-mapping {:ret "int", :sym "SDL_Init", :args [{:type "Uint32", :sym "flags"}]}
                           {:prefixes ["SDL"]})
      (gen-defn {:lib-sym 'sdl-sym}))
  ;;=> [(def init1687 (.getMember sdl-sym "SDL_Init")) (clojure.core/defn init ([flags] (.execute init1687 (clojure.core/object-array [flags]))))]
  )

(defn lib-boilerplate
  [lib-name {:keys [bc-path libs]}]
  (let [lib-sym (gensym (str "lib"))
        context-f-sym (gensym "context-f") 
        source-f-sym (gensym "source-f")]
    {:lib-name lib-name
     :bc-path bc-path
     :libs libs
     :lib-sym lib-sym
     :forms (concat [`(ns ~lib-name
                        (:import org.graalvm.polyglot.Context
                                 org.graalvm.polyglot.Source))
                     `(def ~'empty-array (object-array 0))]
                    [`(defn ~context-f-sym
                        []
                        (-> (org.graalvm.polyglot.Context/newBuilder (into-array ["llvm"]))
                            (.allowIO true)
                            (.allowNativeAccess true)
                            (.build)))
                     `(defn ~source-f-sym
                        []
                        (-> (org.graalvm.polyglot.Source/newBuilder "llvm" (if (string? ~bc-path)
                                                                             (io/file ~bc-path)
                                                                             ~bc-path))
                            (.build)))
                     `(def ~lib-sym (.eval (~context-f-sym) (~source-f-sym)))])}))

(defn gen-lib
  [lib-name fns opts]
  (let [bp (lib-boilerplate lib-name opts)
        defn-forms (apply concat (map #(gen-defn % bp) fns))]
    (concat (:forms bp)
            defn-forms)))

(defn shadow-data
  [f]
  (update f :sym #(str "_SHADOWING_" %)))

(defn gen-c-file
  [includes fns]
  (let [incs (apply str (map #(str "#include<" % ">\n") includes))]
    (str incs
         "\n"
         (str/join "\n" fns))))

(defn gen-both
  [lib-name {:keys [functions includes protos] :as opts}]
  (let [extra-protos (map parse-c-prototype functions)
        shadows (map generate-shadowing-function protos)
        
        c-file (gen-c-file includes (concat functions shadows))

        protos-as-data-shadowed (map shadow-data protos)

        clojure-lib (gen-lib lib-name (concat
                                       (map gen-clojure-mapping extra-protos)
                                       (map #(gen-clojure-mapping % {:prefixes ["_SHADOWING_SDL"]})
                                            protos-as-data-shadowed))
                             opts)]
    {:c-code c-file
     :clojure-lib clojure-lib
     :opts (assoc opts :lib-name lib-name)}))

(defn compile-c
  [{:keys [c-code opts]}]
  (spit (:c-path opts) c-code)
  (let [sh-opts (concat [(str (System/getenv "LLVM_TOOLCHAIN") "/clang") (:c-path opts)]
                        (map #(str "-l" %) (:libs opts))
                        ["-shared" "-fPIC" "-o" (:bc-path opts)])]
    (apply sh sh-opts)))

(defn persist-c
  [res]
  (let [{:keys [err]} (compile-c res)]
    (when (seq err) 
      (println "ERROR:" err)
      (println "Compilation failed:" res)
      (throw (Error. err)))))

(defn persist-clj
  [res]
  (with-open [wrtr (io/writer (str (:clojure-src (:opts res))
                                   "/"
                                   (str/replace (str (:lib-name (:opts res))) "." "/") ".clj"))]
    (.write wrtr ";; This file is autogenerated -- probably shouldn't modify it by hand\n")
    (.write wrtr
            (with-out-str (doseq [f (:clojure-lib res)]
                            (pprint f)
                            (print "\n"))))))

(defn gen-and-persist
  "Takes a lib name as symbol and options required to generate c and clojure code.
  Example call:
  (scl/gen-and-persist
  'c.sdl
  {:protos [(parse-c-prototype \"int SDL_Init(Uint32 flags)\")     ;; c style prototye
           {:ret \"void\", :sym \"SDL_Quit\"}]                     ;; prototype as data
  :includes [\"stdio.h\" \"SDL2/SDL.h\"]
  :c-path \"src/generated.c\"
  :bc-path \"libs/generated.bc\"
  :clojure-src \"src\"
  :libs [\"SDL2\"]})
  "
  [lib-name opts]
  (let [{:keys [opts] :as res} (gen-both lib-name opts)]
    (if (:skip-c-gen opts)
      (println ":skip-c true, no c-code generated.")
      (persist-c res))
    (persist-clj res)
    lib-name))

(comment
  
  (pprint (parse-c-prototype "int SDL_Init(Uint32 flags, a * b, const c d)"))
  
  (pprint (parse-c-prototype "void SDL_Quit()"))
  (pprint (parse-c-prototype "int* SDL_Init(Uint32 flags, a * b, c d)"))
  
  
  
  (-> (parse-c-prototype "int* SDL_Init(Uint32 flags, a * b, c d)")
      generate-shadowing-function
      println)
  
  (-> (parse-c-prototype "int* SDL_Init(Uint32 flags, a * b, c d)")
      (gen-clojure-mapping {:prefixes ["SDL_"]})
      println)
  
  
  
  {'SDLEvent {:type "SDL_Event"}
   
   ;; int SDL_Init(Uint32 flags)
   'init {:ret 'int
          :sym "SDL_Init"
          :args [{:type "Uint32"
                  :sym "flags"}]}
   
   ;; int SDL_PollEvent(SDL_Event* event)
   'poll-event {:ret 'int
                :sym "SDL_PollEvent"
                :args [{:type "SDL_Event"
                        :pointer "*"
                        :sym "event"}]}})
