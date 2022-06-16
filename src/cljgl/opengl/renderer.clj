(ns cljgl.opengl.renderer
  (:require [cljgl.common.debug :as debug]
            [cljgl.common.disposer :as disposer]
            [cljgl.common.gl-util :as gl-util]
            [cljgl.opengl.buffers :as buffers]
            [cljgl.opengl.gl :as gl]
            [cljgl.opengl.shaders :as shaders])
  (:import (cljgl.common.disposer IDisposable)
           (cljgl.opengl.buffers Vao)
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
                   ^Vao
                   vbo vao
                   ebo
                   ^:unsynchronized-mutable ebo-size]
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

(defmulti make-renderer (fn [renderer-type data] renderer-type))

(defmethod make-renderer :static-draw
  [_ {:keys [vertex-data num-attrs order->id indices vertex-buffer-stride attributes-setups
             renderer-lookup-name shader-program VAO]}]
  (let [STATIC-VBO (buffers/static-vbo)
        STATIC-EBO (buffers/static-ebo)
        vertex-data (let [ret (transient [])]
                      (doseq [vertex vertex-data
                              i (range num-attrs)
                              v ((order->id i) vertex)]
                        (conj! ret v))
                      (persistent! ret))
        _ (debug/assert-all (buffers/buffer-data STATIC-VBO (float-array vertex-data))
                            (buffers/buffer-data STATIC-EBO (int-array indices))
                            (reduce (fn [[i byte-offset] {:keys [components gl-type normalize?]}]
                                      (gl/setup-vertex-attribute i components (gl-util/gl-type gl-type)
                                                                 normalize? vertex-buffer-stride byte-offset)

                                      (gl/enable-vertex-attrib-array i)
                                      [(inc i) (+ byte-offset (* components (gl-util/sizeof gl-type)))])
                                    [0 0]
                                    attributes-setups))
        renderer (Renderer. renderer-lookup-name shader-program VAO STATIC-VBO STATIC-EBO (count indices))]
    (swap! renderers-by-id assoc renderer-lookup-name renderer)
    renderer))

(defmethod make-renderer :dynamic-draw
  [_ {:keys [vertex-buffer-stride attributes-setups renderer-lookup-name shader-program VAO]}]
  (let [DYNAMIC-VBO (buffers/dynamic-vbo vertex-buffer-stride)
        DYNAMIC-EBO (buffers/dynamic-ebo)
        _ (debug/assert (reduce (fn [[i byte-offset] {:keys [components gl-type normalize?]}]
                                  (gl/setup-vertex-attribute i components (gl-util/gl-type gl-type)
                                                             normalize? vertex-buffer-stride byte-offset)

                                  (gl/enable-vertex-attrib-array i)
                                  [(inc i) (+ byte-offset (* components (gl-util/sizeof gl-type)))])
                                [0 0]
                                attributes-setups))
        renderer (Renderer. renderer-lookup-name shader-program VAO DYNAMIC-VBO DYNAMIC-EBO 0)]
    (swap! renderers-by-id assoc renderer-lookup-name renderer)
    renderer))

(defn setup-renderer [{:keys [shaders-source-path usage-type vertex-buffer vertex-data indices
                              shader-program-lookup-name renderer-lookup-name]
                       :or   {shader-program-lookup-name (keyword (gensym "shader-program__"))
                              renderer-lookup-name       (keyword (gensym "renderer__"))}}]
  (let [shader-program (shaders/make-shader-program shader-program-lookup-name shaders-source-path)
        {:keys [attributes-setups]} vertex-buffer
        attributes-setups (map-indexed #(assoc %2 :order %1) attributes-setups)
        [vertex-buffer-stride order->id] (reduce (fn [[offset order->id] {:keys [components gl-type id order]}]
                                                   [(+ offset (* components (gl-util/sizeof gl-type)))
                                                    (assoc order->id order id)])
                                                 [0 {}]
                                                 attributes-setups)
        num-attrs (count attributes-setups)
        VAO (doto (buffers/gen-vao) buffers/bind)]
    (make-renderer usage-type
                   (gl-util/identity-keyword-map attributes-setups indices num-attrs order->id renderer-lookup-name
                                                 shader-program VAO vertex-buffer-stride vertex-data))))