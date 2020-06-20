(ns gen-clj
  (:require [clojure.string :as str]
            [gen-c :refer [no-subdir]]))

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


(defn get-type
  [types {:keys [type pointer]}]
  (if-let [t (types type)]
    (if (symbol? t)
      t
      (get t pointer))
    nil))

(defn get-type-throw
  [types t]
  (if-let [t (get-type types t)]
    t
    (throw (Error. (str "No type defined for type: " t)))))

(defn attr->method
  [types {:keys [sym] :as arg}]
  [(with-meta (symbol sym)
     {org.graalvm.nativeimage.c.struct.CField sym}) []
   (get-type-throw types arg)])

(defn struct->gen-interface
  [types {:keys [c-sym clj-sym attrs]} {:keys [lib-name]}]
  (let [java-friendly-lib-name (str/replace lib-name "-" "_")
        context (symbol (str java-friendly-lib-name "_ni.Headers"))]
    `(gen-interface
      :name ~(with-meta (symbol (str lib-name "_ni." clj-sym))
               {org.graalvm.nativeimage.c.CContext context
                org.graalvm.nativeimage.c.function.CLibrary (no-subdir lib-name)
                org.graalvm.nativeimage.c.struct.CStruct c-sym})
      :extends [org.graalvm.word.PointerBase]
      :methods ~(->> (map #(attr->method types %) attrs)
                     (into [])))))

(def convert-function
  {'int '.asInt
   'bindings.sdl_ni.SDL_PixelFormat 'identity
   'org.graalvm.nativeimage.c.type.VoidPointer 'identity})

(defn convert-function-throw
  [t]
  (if-let [cf (convert-function t)] 
    cf
    (throw (Error. (str "No convert-function defined for type: " t)))))

(defn attr->protocol-func
  [{:keys [sym]}]
  `(~(symbol sym) [~'this]))

(defn struct->def-protocol
  [types {:keys [clj-sym attrs]}]
  (concat `(defprotocol
               ~(symbol (str clj-sym "P")))
          (map attr->protocol-func attrs)))

(defn attr->func-implementation
  [types {:keys [sym] :as arg}]
  `(~(symbol sym) [~'this]
    (-> (~'.getMember ~'this ~sym)
        ~(convert-function-throw (get-type-throw types arg)))))

(defn struct->extend-type
  [types {:keys [clj-sym attrs]}]
  (concat `(extend-type org.graalvm.polyglot.Value
             ~(symbol (str clj-sym "P")))
          (map #(attr->func-implementation types %) attrs)))

(comment
  (def types
    {"void" {"*" 'org.graalvm.nativeimage.c.type.VoidPointer
             nil 'void}
     "int" 'int
     "char" {"*" 'org.graalvm.nativeimage.c.type.CCharPointer
             nil 'char}
     "Uint32" 'int
     "Uint8" 'int
     "SDL_Surface" 'bindings.sdl_ni.SDL_Surface
     "SDL_Rect" 'org.graalvm.nativeimage.c.type.VoidPointer
     "SDL_Event" 'bindings.sdl_ni.SDL_Event
     "SDL_Window" 'org.graalvm.nativeimage.c.type.VoidPointer
     "SDL_PixelFormat" 'bindings.sdl_ni.SDL_PixelFormat})
  
  (struct->gen-interface 
   types
   {:c-sym "SDL_Event"
    :clj-sym 'SDL_Event
    :attrs [{:sym "type", :type "int"}]}
   {:lib-name 'bindings.sdl})
  
  (struct->def-protocol
   types
   {:c-sym "SDL_Event"
    :clj-sym 'SDL_Event
    :attrs [{:sym "type", :type "int"}]})
  
  (struct->extend-type
   types
   {:c-sym "SDL_Event"
    :clj-sym 'SDL_Event
    :attrs [{:sym "type", :type "int"}]})
  
  
  )


