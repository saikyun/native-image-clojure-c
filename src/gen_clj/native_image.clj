(ns gen-clj.native-image
  (:require [patch-gen-class :as pgc]
            [clojure.java.io :as io]
            [gen-clj :refer [gen-clojure-mapping get-type-throw]]
            [gen-c :refer [get-h-path snake-case no-subdir]]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint pp]]))

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
  [[f-sym {:keys [ret pointer sym args]}] {:keys [types]}]
  (-> [(with-meta (symbol (str/replace (name f-sym) "-" "_"))
         {'org.graalvm.nativeimage.c.function.CFunction
          {:transition 'org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
           :value sym}})
       (into [] (map (partial get-type-throw types) args))
       (get-type-throw types {:type ret, :pointer pointer})]
      (with-meta {:static true, :native true})))

(comment
  (binding [*print-meta* true]
    (-> (gen-clojure-mapping {:ret "int",
                              :sym "_SHADOWING_SDL_Init",
                              :args [{:type "Uint32", :sym "flags"}]}
                             {:prefixes ["_SHADOWING_SDL_"]})
        (gen-defn {:types types}))))

(defn gen-gen-class
  [{:keys [lib-name]}]
  (let [java-friendly-lib-name (str/replace lib-name "-" "_")]
    `(pgc/gen-class-native
      :name ~(with-meta (symbol (str java-friendly-lib-name "_class"))
               {org.graalvm.nativeimage.c.CContext (symbol (str java-friendly-lib-name "_ni.Headers"))
                org.graalvm.nativeimage.c.function.CLibrary (no-subdir lib-name)}))))

(comment
  (binding [*print-meta* true]
    (prn
     (gen-gen-class {:c-name "generated"
                     :context 'sdl_native.Headers
                     :name 'sdl_native_lib})))
  )

(defn gen-lib
  [{:keys [lib-name clojure-mappings append-ni] :as opts}]
  (concat [`(ns ~(symbol (str (name lib-name) "-ni"))
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
               [~(str "\"" (System/getProperty "user.dir") "/" (get-h-path opts) "\"")]))]
          
          append-ni
          
          [(reverse (into '() (concat (gen-gen-class opts)
                                      [:methods (into [] (map #(gen-defn % opts) clojure-mappings))])))]))

(defn persist-clj
  [{:keys [ni-code lib-name]}]
  (with-open [wrtr (io/writer (str "src"
                                   "/"
                                   (snake-case (str/replace (str lib-name) "." "/")) "_ni.clj"))]
    (.write wrtr ";; This file is autogenerated -- probably shouldn't modify it by hand\n")
    (.write wrtr
            (with-out-str (doseq [f ni-code]
                            (binding [*print-meta* true]
                              (prn f))
                            (print "\n"))))))

(comment
  
  (do (def gl (gen-lib {:c-name "generated"
                        :class-name 'sdl_native_lib
                        :types types
                        :context 'Headers
                        :append-ni [`(gen-interface 
                                      :name ~(with-meta 'sdl_native.SDL_Event
                                               {org.graalvm.nativeimage.c.CContext 'gen_sdl_native_lib.Headers
                                                org.graalvm.nativeimage.c.function.CLibrary "generated"
                                                org.graalvm.nativeimage.c.struct.CStruct "SDL_Event"})
                                      :extends [org.graalvm.word.PointerBase]
                                      :methods [[~(with-meta 'type
                                                    {org.graalvm.nativeimage.c.struct.CField "type"}) []
                                                 ~'int]])
                                    
                                    `(gen-interface 
                                      :name ~(with-meta 'sdl_native.SDL_PixelFormat
                                               {org.graalvm.nativeimage.c.CContext 'gen_sdl_native_lib.Headers
                                                org.graalvm.nativeimage.c.function.CLibrary "generated"
                                                org.graalvm.nativeimage.c.struct.CStruct "SDL_PixelFormat"})
                                      :extends [org.graalvm.word.PointerBase]
                                      :methods [[~(with-meta 'palette
                                                    {org.graalvm.nativeimage.c.struct.CField "palette"}) []
                                                 ~'org.graalvm.nativeimage.c.type.VoidPointer]])
                                    
                                    `(gen-interface 
                                      :name ~(with-meta 'sdl_native.SDL_Surface
                                               {org.graalvm.nativeimage.c.CContext 'gen_sdl_native_lib.Headers
                                                org.graalvm.nativeimage.c.function.CLibrary "generated"
                                                org.graalvm.nativeimage.c.struct.CStruct "SDL_Surface"})
                                      :extends [org.graalvm.word.PointerBase]
                                      :methods [[~(with-meta 'format
                                                    {org.graalvm.nativeimage.c.struct.CField "format"})
                                                 []
                                                 ~'sdl_native.SDL_PixelFormat]])]
                        
                        :protos (->> [{:ret "int",
                                       :sym "_SHADOWING_SDL_Init",
                                       :args [{:type "Uint32", :sym "flags"}]}]
                                     (map #(gen-clojure-mapping % {:prefixes ["_SHADOWING_SDL_"]})))}))
      
      (pprint gl))
  
  (binding [*print-meta* true] (prn gl))
  
  
  
  
  
  (->> gl
       (persist-clj 'gen_sdl_native_lib))
  
  
  
  )
