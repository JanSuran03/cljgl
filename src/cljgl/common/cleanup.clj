(ns cljgl.common.cleanup
  (:require [cljgl.opengl.buffers :as buffers]
            [cljgl.opengl.renderer :as renderer]))

(defn cleanup []
  (renderer/reset)
  (buffers/reset))