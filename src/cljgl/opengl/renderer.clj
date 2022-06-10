(ns cljgl.opengl.renderer
  (:require [cljgl.common.debug :as debug]
            [cljgl.common.disposer :as disposer]
            [cljgl.common.gl-util :as gl-util]
            [cljgl.opengl.buffers :as buffers]
            [cljgl.opengl.gl :as gl]
            [cljgl.opengl.shaders :as shaders])
  (:import (cljgl.common.disposer IDisposable)
           (cljgl.opengl.buffers Vbo Vao Ebo)
           (cljgl.opengl.shaders ShaderProgram)
           (org.lwjgl.opengl GL33)))

(defprotocol IRenderer
  (render [this] "Performs an OpenGL rendering."))

(defn draw-elements
  ([mode count] (draw-elements mode count gl/U-INT 0))
  ([mode count gl-type offset] (GL33/glDrawElements mode count gl-type offset)))

(def rendering-order (atom nil))
(defn change-rendering-order [new-rendering-order] (reset! rendering-order new-rendering-order))

(defonce ^:private renderers-by-id (atom {}))
(defn remove-renderer [renderer-id] (swap! renderers-by-id dissoc renderer-id))
(defn get-renderer [renderer-id] (@renderers-by-id renderer-id))

(defn reset []
  (doseq [[_ renderer] @renderers-by-id]
    (disposer/dispose renderer))
  (reset! renderers-by-id {}))

(deftype Renderer [renderer-id
                   ^ShaderProgram shader-program
                   ^Vbo vbo
                   ^Vao vao
                   ^Ebo ebo ebo-size]
  IRenderer
  (render [this]
    (shaders/use-shader-program shader-program)
    (buffers/bind vao)
    (buffers/bind ebo)
    (draw-elements gl/triangles ebo-size))
  IDisposable
  (disposer/dispose [this]
    (disposer/dispose shader-program)
    (disposer/dispose vbo)
    (disposer/dispose vao)
    (disposer/dispose ebo)
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
  [{:keys [shaders-source-path
           #_floats vertex-positions
           #_ints vertex-positions-indices
           attributes-setups
           shader-program-lookup-name
           renderer-id] :or {renderer-id (name (gensym "renderer_"))}}]
  (let [shader-program (shaders/make-shader-program shader-program-lookup-name shaders-source-path)
        VAO (doto (buffers/gen-vao) buffers/bind)
        VBO (doto (buffers/gen-vbo) buffers/bind)
        EBO (doto (buffers/gen-ebo) buffers/bind)
        vertex-buffer-stride (reduce (fn [offset {:keys [components gl-type]}]
                                       (+ offset (* components (gl-util/sizeof gl-type))))
                                     0
                                     attributes-setups)
        _ (debug/assert-all (buffers/vbo-data (:data vertex-positions) (gl-util/gl-usage (:usage vertex-positions)))
                            (buffers/ebo-data (:data vertex-positions-indices) (gl-util/gl-usage (:usage vertex-positions-indices)))
                            (reduce (fn [[i byte-offset] {:keys [components gl-type normalize?]}]
                                      (gl/setup-vertex-attribute i components (gl-util/gl-type gl-type)
                                                                 normalize? vertex-buffer-stride byte-offset)
                                      (gl/enable-vertex-attrib-array i)

                                      [(inc i) (+ byte-offset (* components (gl-util/sizeof gl-type)))])
                                    [0 0]
                                    attributes-setups))
        renderer (Renderer. renderer-id shader-program VBO VAO EBO (count (:data vertex-positions-indices)))]
    (swap! renderers-by-id assoc renderer-id renderer)
    renderer))