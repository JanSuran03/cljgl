(ns cljgl.common.cleanup
  (:require [cljgl.opengl.buffers :as buffers]
            [cljgl.opengl.renderer :as renderer]
            [cljgl.opengl.shaders :as shaders]))

(defn cleanup []
  (renderer/reset)
  (println "Renderer reset.")
  (buffers/reset)
  (println "Buffers reset.")
  (shaders/unbind-shader-program)
  (println "Shader program unbound."))