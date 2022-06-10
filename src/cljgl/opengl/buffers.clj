(ns cljgl.opengl.buffers
  (:require [cljgl.common.disposer :as disposer])
  (:import (cljgl.common.disposer IDisposable)
           (org.lwjgl.opengl GL33)))

(defonce ^Integer ARRAY-BUFFER GL33/GL_ARRAY_BUFFER)
(defonce ^Integer ELEMENT-ARRAY-BUFFER GL33/GL_ELEMENT_ARRAY_BUFFER)

(defonce ^Integer STATIC-DRAW GL33/GL_STATIC_DRAW)
(defonce ^Integer DYNAMIC-DRAW GL33/GL_DYNAMIC_DRAW)
(defonce ^Integer STREAM-DRAW GL33/GL_STREAM_DRAW)

(defonce current-vbo (atom -1))
(defonce current-vao (atom -1))
(defonce current-ebo (atom -1))
(defn reset [] (doseq [buffer [current-vbo current-vao current-ebo]] (reset! buffer -1)))

(defn bind-buffer [vertex-buffer-object buffer-type] (GL33/glBindBuffer buffer-type vertex-buffer-object))

(defn unbind-array-buffer [] (bind-buffer 0 ARRAY-BUFFER))
(defn unbind-element-array-buffer [] (bind-buffer 0 ELEMENT-ARRAY-BUFFER))

(defn vbo-data
  ([^"[F" data] (GL33/glBufferData ARRAY-BUFFER data STATIC-DRAW))
  ([^"[F" data ^Integer usage] (GL33/glBufferData ARRAY-BUFFER data usage)))

(defn ebo-data
  ([^"[I" data] (GL33/glBufferData ELEMENT-ARRAY-BUFFER data STATIC-DRAW))
  ([^"[I" data ^Integer usage] (GL33/glBufferData ELEMENT-ARRAY-BUFFER data usage)))

(defprotocol IBuffer
  (bind [this]))

(deftype Vbo [vbo-id]
  IBuffer
  (bind [this]
    (when-not (= vbo-id @current-vbo)
      (reset! current-vbo vbo-id)
      (GL33/glBindBuffer ARRAY-BUFFER vbo-id)))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteBuffers ^Integer vbo-id)))

(deftype Vao [vao-id]
  IBuffer
  (bind [this]
    (when-not (= vao-id @current-vao)
      (reset! current-vao vao-id)
      (GL33/glBindVertexArray vao-id)))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteVertexArrays ^Integer vao-id)))

(deftype Ebo [ebo-id]
  IBuffer
  (bind [this]
    (when-not (= ebo-id @current-ebo)
      (reset! current-ebo ebo-id)
      (GL33/glBindBuffer ELEMENT-ARRAY-BUFFER ebo-id)))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteBuffers ^Integer ebo-id)))

(defn gen-vbo [] (Vbo. (GL33/glGenBuffers)))
(defn gen-vao [] (Vao. (GL33/glGenVertexArrays)))
(defn gen-ebo [] (Ebo. (GL33/glGenBuffers)))