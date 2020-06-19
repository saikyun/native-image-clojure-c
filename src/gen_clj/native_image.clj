(ns gen-clj.native-image
  (:require [patch-gen-class :as pgc])
  (:require [gen-clj :refer [gen-clojure-mapping]]
            [clojure.pprint :refer [pprint pp]]))

(def types
  {"void" 'void
   "int" 'int
   "Uint32" 'int})

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
  [[f-sym {:keys [ret sym args]}] {:keys [types]}]
  (-> [(with-meta f-sym
         {org.graalvm.nativeimage.c.function.CFunction
          {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
           :value sym}})
       (into [] (map (comp types :type) args))
       (types ret)]
      (with-meta {:static true, :native true})))

(comment
  (binding [*print-meta* true]
    (-> (gen-clojure-mapping {:ret "int",
                              :sym "_SHADOWING_SDL_Init",
                              :args [{:type "Uint32", :sym "flags"}]}
                             {:prefixes ["_SHADOWING_SDL_"]})
        (gen-defn {:types types}))))

(defn gen-gen-class
  [{:keys [lib context name]}]
  `(pgc/gen-class-native
    :name (with-meta ~name
            {org.graalvm.nativeimage.c.CContext ~context
             org.graalvm.nativeimage.c.function.CLibrary ~lib})))

(comment
  (binding [*print-meta* true]
    (prn
     (gen-gen-class {:lib "generated"
                     :context 'sdl_native.Headers
                     :name 'sdl_native_lib})))
  )

(defn gen-lib
  [{:keys [fns] :as opts}]
  (concat
   (gen-gen-class opts)
   [:methods (into [] (map #(gen-defn % opts) fns))]))

(comment
  (binding [*print-meta* true]
    (prn
     (gen-lib {:lib "generated"
               :context 'sdl_native.Headers
               :name 'sdl_native_lib
               :types types
               :fns (->> [{:ret "int",
                           :sym "_SHADOWING_SDL_Init",
                           :args [{:type "Uint32", :sym "flags"}]}]
                         (map #(gen-clojure-mapping % {:prefixes "_SHADOWING_SDL_"})))})))
  
  (eval (gen-lib {:lib "generated"
                  :context 'sdl_native.Headers
                  :name 'sdl_native_lib2
                  :types types
                  :fns (->> [{:ret "int",
                              :sym "_SHADOWING_SDL_Init",
                              :args [{:type "Uint32", :sym "flags"}]}]
                            (map #(gen-clojure-mapping % {:prefixes "_SHADOWING_SDL_"})))}))
  
  )
