(ns native-interop)

(def ^:dynamic *native-image* 
  (if (or (some? (System/getProperty "org.graalvm.nativeimage.kind"))
          (and (some? (System/getenv "NATIVE_IMAGE"))
               (not= "false" (System/getenv "NATIVE_IMAGE"))))
    (alter-var-root #'native-interop/*native-image* (constantly true))
    (alter-var-root #'native-interop/*native-image* (constantly false))))
