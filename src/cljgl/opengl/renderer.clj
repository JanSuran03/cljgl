(ns cljgl.opengl.renderer
  (:require [cljgl.common.debug :as debug]
            [cljgl.common.destructor :as destructor]
            [cljgl.common.gl-util :as gl-util]
            [cljgl.opengl.buffers :as buffers]
            [cljgl.opengl.gl :as gl]
            [cljgl.opengl.shaders :as shaders])
  (:import (cljgl.common.destructor IDestructor)
           (cljgl.opengl.shaders ShaderProgram)
           (org.lwjgl.opengl GL33)))

(defprotocol IRenderer
  (render [this] "Performs an OpenGL rendering."))

(defn draw-elements [mode count] (GL33/glDrawElements mode count gl/U-INT 0))

(def rendering-order (atom nil))
(defonce ^:private renderers-by-id (atom {}))
(defn remove-renderer [renderer-id] (swap! renderers-by-id dissoc renderer-id))
(defn get-renderer [renderer-id] (@renderers-by-id renderer-id))

(defn reset []
  (doseq [[_ renderer] @renderers-by-id]
    (destructor/destroy renderer))
  (reset! renderers-by-id {}))

(deftype Renderer [renderer-id ^ShaderProgram shader-program vao ebo ebo-size]
  IRenderer
  (render [this]
    (shaders/use-shader-program shader-program)
    (buffers/bind-VAO vao)
    (buffers/bind-EBO ebo)
    (draw-elements gl/triangles ebo-size))
  IDestructor
  (gl-util/destroy [this]
    (destructor/destroy shader-program)
    ;(util/destroy vao)
    ;(util/destroy ebo)
    (remove-renderer renderer-id)))

(defn render-all []
  (doseq [renderer-id @rendering-order]
    (when-let [renderer (get-renderer renderer-id)]
      (render renderer))))

(defn setup-renderer
  "(renderer/setup-rendering
   {:shaders-source-path   \"resources/shaders/triangle.glsl\"
   :vertex-positions     (list -0.5 -0.5, 0.5 -0.5, +0.5 +0.5, -0.5 0.5)
   :attributes-setups    [{:components 2           ;; layout (location = 0) in vec2;
                           :data-type  gl/FLOAT
                           :normalize? false}
                          {:components 1           ;; layout (location = 1) in float;
                           :data-type  gl/FLOAT
                           :normalize? false}]})"
  [{:keys [shaders-source-path vertex-positions vertex-positions-indices
           attributes-setups shader-program-lookup-name renderer-id] :or {renderer-id (name (gensym "renderer_"))}}]
  (let [shader-program (shaders/make-shader-program shader-program-lookup-name shaders-source-path)
        VAO (buffers/gen-vao)
        VBO (buffers/gen-buffer)
        EBO (buffers/gen-buffer)
        vertex-buffer-stride (reduce (fn [offset {:keys [components gl-type]}]
                                       (+ offset (* components (gl-util/sizeof gl-type))))
                                     0
                                     attributes-setups)
        _ (debug/assert-all (buffers/bind-VAO VAO)
                            (buffers/bind-VBO VBO)
                            (buffers/bind-EBO EBO)
                            (buffers/VBO-data (float-array vertex-positions) :static-draw)
                            (buffers/EBO-data (int-array vertex-positions-indices))
                            (reduce (fn [[i byte-offset] {:keys [components gl-type normalize?]}]
                                      (gl/setup-vertex-attribute i components (gl-util/gl-type gl-type)
                                                                 normalize? vertex-buffer-stride byte-offset)
                                      (gl/enable-vertex-attrib-array i)

                                      [(inc i) (+ byte-offset (* components (gl-util/sizeof gl-type)))])
                                    [0 0]
                                    attributes-setups))
        renderer (Renderer. renderer-id shader-program VAO EBO (count vertex-positions-indices))]
    (swap! renderers-by-id assoc renderer-id renderer)
    renderer))