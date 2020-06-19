(ns create-sdl-ns
  (:require [spec-c-lib :as scl]
            [parse-c :as pc]
            [clojure.string :as str]
            
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


(defn -main
  []
  (println "Creating src/c")
  (.mkdir (java.io.File. "src/c"))
  (println "Creating libs")
  (.mkdir (java.io.File. "libs"))
  
  (println "Generating c.sdl")
  (scl/gen-and-persist
   'c.sdl
   {:inline-c (str/join "\n" functions)
    :protos (concat (map pc/parse-c-prototype functions)
                    (map pc/parse-c-prototype prototypes) ;; utility function for turning c-prototypes into clojure data
                    [{:ret "void", :sym "SDL_Quit"}] ;; we can also just provide the data manually
                    )
    :includes ["stdio.h" "SDL2/SDL.h"]
    :c-name "generated"
    :c-path "src/generated.c"
    :h-path "src/generated.h"
    :bc-path "libs/libgenerated.so"
    :clojure-src "src"
    :libs ["SDL2"]})
  
  (println "Done!")
  
  (throw (Error. "Ugly fix -- for some reason it won't quit unless I throw an error.")))

(comment
  (-main)
  )

