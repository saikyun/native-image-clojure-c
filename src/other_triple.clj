(ns other-triple
  (:import org.graalvm.word.PointerBase
           org.graalvm.nativeimage.c.struct.CField
           org.graalvm.nativeimage.c.CContext
           org.graalvm.nativeimage.c.function.CFunction
           org.graalvm.nativeimage.c.function.CLibrary
           org.graalvm.nativeimage.c.struct.CFieldAddress
           org.graalvm.nativeimage.c.struct.CStruct
           org.graalvm.nativeimage.c.struct.AllowWideningCast
           org.graalvm.nativeimage.c.function.CFunction
           Headers)
  (:gen-class))

(gen-interface 
 :name ^{org.graalvm.nativeimage.c.CContext Headers
         org.graalvm.nativeimage.c.function.CLibrary "triple"
         org.graalvm.nativeimage.c.struct.CStruct "value_t"}
 wat.cool.OOValue
 :extends [org.graalvm.word.PointerBase]
 :methods [[^{org.graalvm.nativeimage.c.struct.CField "id"
              org.graalvm.nativeimage.c.struct.AllowWideningCast true} getId [] long]])

(gen-interface 
 :name ^{org.graalvm.nativeimage.c.CContext Headers
         org.graalvm.nativeimage.c.function.CLibrary "triple"
         org.graalvm.nativeimage.c.struct.CStruct "triple_t"}
 wat.cool.OOTriple
 :extends [org.graalvm.word.PointerBase]
 :methods [[^{org.graalvm.nativeimage.c.struct.CFieldAddress "subject"} subject [] wat.cool.OOValue]])

(gen-class
 :name ^{org.graalvm.nativeimage.c.CContext Headers
         org.graalvm.nativeimage.c.function.CLibrary "triple"}
 wat.cool.OOTripletLib

 :methods [^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                              allocRandomTriple
                              []
                              wat.cool.OOTriple]
           ^:static ^:native [^{org.graalvm.nativeimage.c.function.CFunction
                                {:transition org.graalvm.nativeimage.c.function.CFunction$Transition/NO_TRANSITION}}
                              freeTriple
                              [wat.cool.OOTriple]
                              void]])



;; (comment
;;   public class TripletLib {
;;                            @CEnum("type_t")
;;                            enum DataType {
;;                                           I,
;;                                           F,
;;                                           S;

;;                                           @CEnumValue
;;                                           public native int getCValue();

;;                                           @CEnumLookup
;;                                           public static native DataType fromCValue(int value);
;;                                           }

;;                            @CFunction(transition = Transition.NO_TRANSITION)
;;                            public static native OOTriple allocRandomTriple();

;;                            @CFunction(transition = Transition.NO_TRANSITION)
;;                            public static native void freeTriple(OOTriple triple);

;;                            public static long getThing(OOTriple triple) {
;;                                                                          return triple.subject().getId();
;;                                                                          }
;;                            }
;;   )
