(ns native-interop)

(def ^:dynamic *native-image* false)

(defmacro nget
  [v attr]
  (if *native-image*
    `(~(symbol (str "." (name attr))) ~v)
    `(~attr ~v)))
