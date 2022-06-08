(ns cljgl.common.util
  (:import (org.lwjgl.system MemoryUtil)))

(defonce ^Long null MemoryUtil/NULL)