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

(defn sizeof [gl-type-kw]
  (case gl-type-kw
    (:gl-float gl/FLOAT :gl-unsigned-int
      gl/U-INT :gl-int gl/INT) 4
    (:gl-byte gl/BYTE :gl-unsigned-byte gl/U-BYTE) 1))

(defn gl-type [gl-type-kw]
  ({:gl-float        gl/FLOAT
    :gl-int          gl/INT
    :gl-unsigned-int gl/U-INT
    :gl-byte         gl/U-BYTE}
   gl-type-kw))