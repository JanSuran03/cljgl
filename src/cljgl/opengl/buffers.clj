(ns cljgl.opengl.buffers
  (:import (org.lwjgl.opengl GL33)))

(defonce ^Integer ARRAY-BUFFER GL33/GL_ARRAY_BUFFER)
(defonce ^Integer ELEMENT-ARRAY-BUFFER GL33/GL_ELEMENT_ARRAY_BUFFER)
(defn bind-buffer [vertex-buffer-object buffer-type] (GL33/glBindBuffer buffer-type vertex-buffer-object))

(defn unbind-buffer [buffer-type] (bind-buffer 0 (case buffer-type
                                                   :vertex-buffer ARRAY-BUFFER
                                                   (:element-buffer :index-buffer) ELEMENT-ARRAY-BUFFER)))
(defn bind-VBO
  "Makes the currently used vertex buffer object refer to a VBO with the given id."
  [vertex-buffer-object]
  (bind-buffer vertex-buffer-object ARRAY-BUFFER))

(defn bind-VAO
  "Makes the currently used vertex array object refer to a VAO with the given id."
  [vertex-array-object]
  (GL33/glBindVertexArray vertex-array-object))
;; EBO
(defn bind-EBO
  "Makes the currently used element buffer object refer to an EBO with the given id."
  [element-buffer-object]
  (bind-buffer element-buffer-object ELEMENT-ARRAY-BUFFER))
(defn buffer-data [^Integer target-buffer ^"[F" data ^Integer draw-type]
  (GL33/glBufferData target-buffer data draw-type))

(defn VBO-data [^"[F" data draw-type]
  (buffer-data ARRAY-BUFFER data (case draw-type
                                   :static-draw GL33/GL_STATIC_DRAW
                                   :stream-draw GL33/GL_STREAM_DRAW
                                   :dynamic-draw GL33/GL_DYNAMIC_DRAW)))

(defn EBO-data [^"[I" data]
  (GL33/glBufferData ELEMENT-ARRAY-BUFFER data GL33/GL_STATIC_DRAW))

(defn gen-buffer []
  (GL33/glGenBuffers))

(defn gen-vao []
  (GL33/glGenVertexArrays))