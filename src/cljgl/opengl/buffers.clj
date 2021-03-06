(ns cljgl.opengl.buffers
  (:require [cljgl.common.disposer :as disposer]
            [cljgl.common.gl-util :as gl-util]
            [cljgl.opengl.gl :as gl])
  (:import (cljgl.common.disposer IDisposable)
           (org.lwjgl.opengl GL33)))

(defonce ^Integer ARRAY-BUFFER GL33/GL_ARRAY_BUFFER)
(defonce ^Integer ELEMENT-ARRAY-BUFFER GL33/GL_ELEMENT_ARRAY_BUFFER)

(defonce current-vbo (atom -1))
(defonce current-vao (atom -1))
(defonce current-ebo (atom -1))
(defn reset [] (doseq [buffer [current-vbo current-vao current-ebo]] (reset! buffer -1)))

(defprotocol IBuffer
  (bind [this])
  (unbind [this])
  (buffer-data [this data] [this data byte-size])
  (malloc [this byte-size]))

(deftype StaticVbo [vbo-id]
  IBuffer
  (bind [this]
    (when-not (= vbo-id @current-vbo)
      (reset! current-vbo vbo-id)
      (GL33/glBindBuffer ARRAY-BUFFER vbo-id)))
  (unbind [this] (GL33/glBindBuffer ARRAY-BUFFER 0))
  (buffer-data [this data]
    (bind this)
    (GL33/glBufferData ARRAY-BUFFER ^"[F" data gl/STATIC-DRAW))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteBuffers ^Integer vbo-id)))

(deftype DynamicVbo [vbo-id vertex-stride ^:unsynchronized-mutable offset]
  IBuffer
  (bind [this]
    (when-not (= vbo-id @current-vbo)
      (reset! current-vbo vbo-id)
      (GL33/glBindBuffer ARRAY-BUFFER vbo-id)))
  (unbind [this] (GL33/glBindBuffer ARRAY-BUFFER 0))
  (malloc [this byte-size]
    (bind this)
    (GL33/glBufferData ARRAY-BUFFER ^long byte-size gl/DYNAMIC-DRAW))
  (buffer-data [this data byte-size]
    (bind this)
    (GL33/glBufferSubData ARRAY-BUFFER ^int offset ^floats data)
    (set! offset (+ offset byte-size)))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteBuffers ^Integer vbo-id)))

(deftype Vao [vao-id]
  IBuffer
  (bind [this]
    (when-not (= vao-id @current-vao)
      (reset! current-vao vao-id)
      (GL33/glBindVertexArray vao-id)))
  (unbind [this] (GL33/glBindBuffer ARRAY-BUFFER 0))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteVertexArrays ^Integer vao-id)))

(deftype StaticEbo [ebo-id]
  IBuffer
  (bind [this]
    (when-not (= ebo-id @current-ebo)
      (reset! current-ebo ebo-id)
      (GL33/glBindBuffer ELEMENT-ARRAY-BUFFER ebo-id)))
  (unbind [this] (GL33/glBindBuffer ELEMENT-ARRAY-BUFFER 0))
  (buffer-data [this data]
    (bind this)
    (GL33/glBufferData ELEMENT-ARRAY-BUFFER ^"[I" data gl/STATIC-DRAW))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteBuffers ^Integer ebo-id)))

(deftype DynamicEbo [ebo-id ^:unsynchronized-mutable offset]
  IBuffer
  (bind [this]
    (when-not (= ebo-id @current-ebo)
      (reset! current-ebo ebo-id)
      (GL33/glBindBuffer ELEMENT-ARRAY-BUFFER ebo-id)))
  (unbind [this] (GL33/glBindBuffer ELEMENT-ARRAY-BUFFER 0))
  (malloc [this byte-size]
    (bind this)
    (GL33/glBufferData ELEMENT-ARRAY-BUFFER ^int byte-size gl/DYNAMIC-DRAW))
  (buffer-data [this data byte-size]
    (bind this)
    (GL33/glBufferSubData ELEMENT-ARRAY-BUFFER ^int offset ^ints data)
    (set! offset (+ offset byte-size)))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteBuffers ^Integer ebo-id)))

(defn static-vbo [] (StaticVbo. (GL33/glGenBuffers)))
(defn dynamic-vbo [vertex-stride] (DynamicVbo. (GL33/glGenBuffers) vertex-stride 0))
(defn gen-vao [] (Vao. (GL33/glGenVertexArrays)))
(defn static-ebo [] (StaticEbo. (GL33/glGenBuffers)))
(defn dynamic-ebo [] (DynamicEbo. (GL33/glGenBuffers) 0))