(ns gen-clj.native-image
  (:require [patch-gen-class :as pgc]
            [clojure.java.io :as io]
            [gen-clj :refer [gen-clojure-mapping]]
            [clojure.string :as str]
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
  (-> [(with-meta (symbol (str/replace (name f-sym) "-" "_"))
         {'org.graalvm.nativeimage.c.function.CFunction
          {:transition 'org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
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
  [{:keys [lib context class-name]}]
  `(pgc/gen-class-native
    :name ~(with-meta class-name
             {org.graalvm.nativeimage.c.CContext context
              org.graalvm.nativeimage.c.function.CLibrary lib})))

(comment
  (binding [*print-meta* true]
    (prn
     (gen-gen-class {:lib "generated"
                     :context 'sdl_native.Headers
                     :name 'sdl_native_lib})))
  )

(defn gen-lib
  [{:keys [class-name fns h-path] :as opts}]
  [`(ns ~(symbol (str "gen-" (name class-name)))
      (:require [~'patch-gen-class])
      (:import org.graalvm.word.PointerBase
               org.graalvm.nativeimage.c.struct.CField
               org.graalvm.nativeimage.c.CContext
               org.graalvm.nativeimage.c.function.CFunction
               org.graalvm.nativeimage.c.function.CLibrary
               org.graalvm.nativeimage.c.struct.CFieldAddress
               org.graalvm.nativeimage.c.struct.CStruct
               org.graalvm.nativeimage.c.struct.AllowWideningCast
               org.graalvm.nativeimage.c.function.CFunction
               org.graalvm.word.WordFactory
               
               [org.graalvm.nativeimage.c.type ~'CCharPointer ~'VoidPointer])
      (:gen-class))
   
   `(deftype ~'Headers
        []
      org.graalvm.nativeimage.c.CContext$Directives
      (~'getHeaderFiles
       [~'_]
       [~(str "\"" (System/getProperty "user.dir") "/" h-path "\"")]))
   
   (reverse (into '() (concat (gen-gen-class opts)
                              [:methods (into [] (map #(gen-defn % opts) fns))])))])

(defn persist-clj
  [lib-name code]
  (with-open [wrtr (io/writer (str "src"
                                   "/"
                                   (str/replace (str lib-name) "." "/") ".clj"))]
    (.write wrtr ";; This file is autogenerated -- probably shouldn't modify it by hand\n")
    (.write wrtr
            (with-out-str (doseq [f code]
                            (binding [*print-meta* true]
                              (prn f))
                            (print "\n"))))))

(comment
  
  (do (def gl (gen-lib {:lib "generated"
                        :context 'Headers
                        :class-name 'sdl_native_lib
                        :types types
                        :h-path "src/generated.h"
                        :fns (->> [{:ret "int",
                                    :sym "_SHADOWING_SDL_Init",
                                    :args [{:type "Uint32", :sym "flags"}]}]
                                  (map #(gen-clojure-mapping % {:prefixes ["_SHADOWING_SDL_"]})))}))
      
      (pprint gl))
  
  (binding [*print-meta* true] (prn gl))
  
  
  
  
  
  (->> gl
       (persist-clj 'gen_sdl_native_lib))
  
  
  
  )
