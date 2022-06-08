(ns cljgl.common.util
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