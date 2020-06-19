(ns gen-clj.polyglot
  (:require [gen-clj :refer [gen-clojure-mapping]]))

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
                        (:require [clojure.java.io])
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
                                                                             (clojure.java.io/file ~bc-path)
                                                                             ~bc-path))
                            (.build)))
                     `(def ~lib-sym (.eval (~context-f-sym) (~source-f-sym)))])}))

(defn gen-lib
  [lib-name fns opts]
  (let [bp (lib-boilerplate lib-name opts)
        defn-forms (apply concat (map #(gen-defn % bp) fns))]
    (concat (:forms bp)
            defn-forms)))
