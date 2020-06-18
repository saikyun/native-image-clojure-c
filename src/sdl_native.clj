(ns sdl-native
  (:require [patch-gen-class])
  (:import org.graalvm.word.PointerBase
           org.graalvm.nativeimage.c.struct.CField
           org.graalvm.nativeimage.c.CContext
           org.graalvm.nativeimage.c.function.CFunction
           org.graalvm.nativeimage.c.function.CLibrary
           org.graalvm.nativeimage.c.struct.CFieldAddress
           org.graalvm.nativeimage.c.struct.CStruct
           org.graalvm.nativeimage.c.struct.AllowWideningCast
           org.graalvm.nativeimage.c.function.CFunction)
  (:gen-class))

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
                              void]])
