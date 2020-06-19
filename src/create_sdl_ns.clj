(ns create-sdl-ns
  (:require [spec-c-lib :as scl]
            [parse-c :as pc]
            [clojure.string :as str]
            
            [gen-clj.native-image :as ni]
            
            [clojure.pprint :refer [pp pprint]]))

(def functions
  ["int GET_SDL_INIT_VIDEO() { return SDL_INIT_VIDEO; }"
   
   "int GET_SDL_WINDOW_SHOWN() { return SDL_WINDOW_SHOWN; }"
   
   "void* get_null() { return NULL; }"
   
   "char *gen_title() { return \"SDL Tutorial\"; }"
   
   "
SDL_Rect *create_rect(int x, int y, int w, int h) {
  SDL_Rect *r = (SDL_Rect*)malloc(sizeof(SDL_Rect));
  r->x = x;
  r->y = y;
  r->w = w;
  r->h = h;
  return r;
}"
   
   "
SDL_Event e;

SDL_Event *get_e() {
  return &e;
}"])

(def prototypes
  ["int SDL_Init(Uint32 flags)"
   "int SDL_PollEvent(SDL_Event* event)"
   "void SDL_Delay(Uint32 ms)"
   "int SDL_UpdateWindowSurface(SDL_Window* window)"
   "SDL_Surface* SDL_GetWindowSurface(SDL_Window* window)"
   
   "
Uint32 SDL_MapRGB(const SDL_PixelFormat* format,
                  Uint8                  r, 
                  Uint8                  g, 
                  Uint8                  b)
"
   
   "
SDL_Window* SDL_CreateWindow(const char* title,
                             int         x,
                             int         y,
                             int         w,
                             int         h,
                             Uint32      flags)
"
   
   "
int SDL_FillRect(SDL_Surface*    dst,
                 const SDL_Rect* rect,
                 Uint32          color)
"
   
   ])

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

(def interfaces [`(gen-interface 
                   :name ~(with-meta 'bindings.sdl_ni.SDL_Event
                            {org.graalvm.nativeimage.c.CContext 'bindings.sdl_ni.Headers
                             org.graalvm.nativeimage.c.function.CLibrary "bindings$sdl"
                             org.graalvm.nativeimage.c.struct.CStruct "SDL_Event"})
                   :extends [org.graalvm.word.PointerBase]
                   :methods [[~(with-meta 'type
                                 {org.graalvm.nativeimage.c.struct.CField "type"}) []
                              ~'int]])
                 
                 `(gen-interface 
                   :name ~(with-meta 'bindings.sdl_ni.SDL_PixelFormat
                            {org.graalvm.nativeimage.c.CContext 'bindings.sdl_ni.Headers
                             org.graalvm.nativeimage.c.function.CLibrary "bindings$sdl"
                             org.graalvm.nativeimage.c.struct.CStruct "SDL_PixelFormat"})
                   :extends [org.graalvm.word.PointerBase]
                   :methods [[~(with-meta 'palette
                                 {org.graalvm.nativeimage.c.struct.CField "palette"}) []
                              ~'org.graalvm.nativeimage.c.type.VoidPointer]])
                 
                 `(gen-interface 
                   :name ~(with-meta 'bindings.sdl_ni.SDL_Surface
                            {org.graalvm.nativeimage.c.CContext 'bindings.sdl_ni.Headers
                             org.graalvm.nativeimage.c.function.CLibrary "bindings$sdl"
                             org.graalvm.nativeimage.c.struct.CStruct "SDL_Surface"})
                   :extends [org.graalvm.word.PointerBase]
                   :methods [[~(with-meta 'format
                                 {org.graalvm.nativeimage.c.struct.CField "format"})
                              []
                              ~'bindings.sdl_ni.SDL_PixelFormat]])])

(defn -main
  []
  (println "Creating libs")
  (.mkdir (java.io.File. "libs"))
  
  (println "Generating bindings.sdl")
  (let [opts (scl/gen-and-persist
              {:inline-c (str/join "\n" functions)
               :protos (concat (map pc/parse-c-prototype functions)
                               (map pc/parse-c-prototype prototypes) ;; utility function for turning c-prototypes into clojure data
                               [{:ret "void", :sym "SDL_Quit"}] ;; we can also just provide the data manually
                               )
               :includes ["stdio.h" "SDL2/SDL.h"]
               :append-ni interfaces
               :types types
               :lib-name 'bindings.sdl
               :src-dir "src"
               :lib-dir "libs"
               :libs ["SDL2"]})
        opts (assoc opts :ni-code (ni/gen-lib opts))]
    (ni/persist-clj opts)
    opts)
  
  (println "Done!")
  
  (throw (Error. "Ugly fix -- for some reason it won't quit unless I throw an error.")))

(comment
  (-main)
  )

