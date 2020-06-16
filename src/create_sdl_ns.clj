(require '[parse-c :as pc])

(.mkdir (java.io.File. "src/c"))

(pc/persist-lib-res
 "src/c/sdl.clj"
 (pc/load-lib 'c.sdl "src/sdl_starter.c" "sdl_starter.o"
              {:prefix ["SDL_" "_SDL_"]
               :kebab true
               :libs ["SDL2"]}))
