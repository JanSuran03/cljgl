(ns cljgl.common.cleanup
  (:require [cljgl.opengl.buffers :as buffers]
            [cljgl.opengl.renderer :as renderer]
            [cljgl.opengl.shaders :as shaders]
            [cljgl.opengl.textures :as textures]))

(defn cleanup []
  (renderer/reset)
  (println "Renderer reset.")
  (buffers/reset)
  (println "Buffers reset.")
  (shaders/unbind-shader-program)
  (println "Shader program unbound.")
  (textures/delete-textures)
  (println "Textures deleted."))=