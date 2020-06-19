(ns sdl-native
  (:require [patch-gen-class :as pgc]
            [gen-clj.native-image :as ni]
            [gen-clj :as gclj :refer [gen-clojure-mapping]])
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
           
           [org.graalvm.nativeimage.c.type CCharPointer VoidPointer])  
  (:gen-class))

(require 'gen_sdl_native_lib)

(gen-interface 
 :name ^{org.graalvm.nativeimage.c.CContext gen_sdl_native_lib.Headers
         org.graalvm.nativeimage.c.function.CLibrary "generated"
         org.graalvm.nativeimage.c.struct.CStruct "SDL_Event"}
 sdl_native.SDL_Event
 :extends [org.graalvm.word.PointerBase]
 :methods [[^{org.graalvm.nativeimage.c.struct.CField "type"}
            type []
            int]])

(gen-interface 
 :name ^{org.graalvm.nativeimage.c.CContext gen_sdl_native_lib.Headers
         org.graalvm.nativeimage.c.function.CLibrary "generated"
         org.graalvm.nativeimage.c.struct.CStruct "SDL_PixelFormat"}
 sdl_native.SDL_PixelFormat
 :extends [org.graalvm.word.PointerBase]
 :methods [[^{org.graalvm.nativeimage.c.struct.CField "palette"}
            palette []
            org.graalvm.nativeimage.c.type.VoidPointer]])

(gen-interface 
 :name ^{org.graalvm.nativeimage.c.CContext gen_sdl_native_lib.Headers
         org.graalvm.nativeimage.c.function.CLibrary "generated"
         org.graalvm.nativeimage.c.struct.CStruct "SDL_Surface"}
 sdl_native.SDL_Surface
 :extends [org.graalvm.word.PointerBase]
 :methods [[^{org.graalvm.nativeimage.c.struct.CField "format"}
            format
            []
            sdl_native.SDL_PixelFormat]])

#_(def types
    {"void" 'void
     "int" 'int
     "Uint32" 'int})

#_(pgc/gen-class-native
   :name ^{org.graalvm.nativeimage.c.CContext sdl_native.Headers
           org.graalvm.nativeimage.c.function.CLibrary "generated"}
   sdl_native_lib
   
   :methods [^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                _SHADOWING_SDL_Init
                                [int]
                                void]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_Quit"}}
                                sdl_quit
                                []
                                void]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                GET_SDL_INIT_VIDEO
                                []
                                int]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                GET_SDL_WINDOW_SHOWN
                                []
                                int]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                gen_title
                                []
                                org.graalvm.nativeimage.c.type.CCharPointer]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_CreateWindow"}}
                                create_window
                                [org.graalvm.nativeimage.c.type.CCharPointer, int, int, int, int, int]
                                org.graalvm.nativeimage.c.type.VoidPointer
                                #_int]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_GetWindowSurface"}}
                                get_window_surface
                                [org.graalvm.nativeimage.c.type.VoidPointer]
                                sdl_native.SDL_Surface]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                create_rect
                                [int int int int]
                                org.graalvm.nativeimage.c.type.VoidPointer]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_MapRGB"}}
                                map_rgb
                                [org.graalvm.nativeimage.c.type.VoidPointer #_ sdl_native.SDL_PixelFormat int int int]
                                int]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_FillRect"}}
                                fill_rect
                                [org.graalvm.nativeimage.c.type.VoidPointer
                                 org.graalvm.nativeimage.c.type.VoidPointer
                                 int]
                                int]
             
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                get_null
                                []
                                org.graalvm.nativeimage.c.type.VoidPointer]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_PollEvent"}}
                                poll_event
                                [org.graalvm.nativeimage.c.type.VoidPointer]
                                int]
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                get_e
                                []
                                sdl_native.SDL_Event]
             
             
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION
                                   :value "_SHADOWING_SDL_UpdateWindowSurface"}}
                                update_window_surface
                                [org.graalvm.nativeimage.c.type.VoidPointer]
                                int]])

(defn null
  []
  (org.graalvm.word.WordFactory/nullPointer))
(comment
  (defn init [flags] (sdl_native_lib/_SHADOWING_SDL_Init flags))
  (defn get-sdl-init-video [] (sdl_native_lib/GET_SDL_INIT_VIDEO))
  (defn get-sdl-window-shown [] (sdl_native_lib/GET_SDL_WINDOW_SHOWN))
  (defn gen-title ^org.graalvm.nativeimage.c.type.CCharPointer [] (sdl_native_lib/gen_title)))
#_(defn create-window [title x y w h flag]
    (sdl_native_lib/_SHADOWING_SDL_CreateWindow title x y w h flag))

(comment
  
  
  (deftype Headers
      []
    org.graalvm.nativeimage.c.CContext$Directives
    (getHeaderFiles
      [this]
      [(str "\"" (System/getProperty "user.dir") "/src/sdl_starter.h\"")]))
  
  (gen-class-native
   :name ^{org.graalvm.nativeimage.c.CContext sdl_native.Headers
           org.graalvm.nativeimage.c.function.CLibrary "sdl_starter"}
   sdl_native_lib
   
   :methods [^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                beginning
                                []
                                void]
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                middle
                                []
                                void]
             ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                  {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                                end
                                []
                                void]]))
