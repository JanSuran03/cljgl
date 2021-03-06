(ns cljgl.common.gl-util
  (:require [cljgl.opengl.gl :as gl])
  (:import (org.lwjgl.system MemoryUtil)))

(defonce ^Long null MemoryUtil/NULL)

(defn ok?
  "Equal to (= x GL33/GL_TRUE)."
  [x] (= x 1))

(defn log
  ([message] (log message false))
  ([message important?]
   (.println System/err (if important? (str "***************************************\n"
                                            "LOG: " message
                                            "\n***************************************")
                                       message))))

(let [m (into {} (map (fn [[k v]] [k (/ v 8)])
                      {:gl-float         Float/SIZE
                       :gl-unsigned-int  Integer/SIZE
                       :gl-int           Integer/SIZE
                       :gl-byte          Byte/SIZE
                       :gl-unsigned-byte Byte/SIZE}))]
  (defn sizeof [gl-type-kw]
    (m gl-type-kw)))

(defn gl-type [gl-type-kw]
  ({:gl-byte         gl/U-BYTE
    :gl-float        gl/FLOAT
    :gl-int          gl/INT
    :gl-unsigned-int gl/U-INT}
   gl-type-kw))

(defn gl-usage [gl-usage-kw]
  ({:dynamic-draw gl/DYNAMIC-DRAW
    :static-draw  gl/STATIC-DRAW
    :stream-draw  gl/STREAM-DRAW}
   gl-usage-kw))

(defmacro identity-keyword-map [& syms]
  `(hash-map ~@(interleave (map keyword syms) syms)))