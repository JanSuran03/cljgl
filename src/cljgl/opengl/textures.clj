(ns cljgl.opengl.textures
  (:refer-clojure :exclude [get])
  (:require [cljgl.opengl.gl :as gl]
            [cljgl.common.disposer :as disposer]
            [cljgl.common.debug :as debug]
            [me.raynes.fs :as fs])
  (:import (org.lwjgl.opengl GL33)
           (java.nio ByteBuffer)
           (org.lwjgl.stb STBImage)
           (cljgl.common.disposer IDisposable)
           (java.io FileNotFoundException)))

(alias 'core 'clojure.core)

(defonce MAX-OPENGL-TEXTURE-SLOTS (inc (- GL33/GL_TEXTURE31 GL33/GL_TEXTURE0)))

(defonce textures-by-lookup-id (atom {}))
(defn get [texture-lookup-id] (core/get @textures-by-lookup-id texture-lookup-id))
(defonce textures-by-slot (object-array MAX-OPENGL-TEXTURE-SLOTS))

(def ^:private ^Integer tex-2d GL33/GL_TEXTURE_2D)
(defn- gen-texture [] (GL33/glGenTextures))
(defn- bind-texture [texture-id] (GL33/glBindTexture tex-2d texture-id))
(defn- unbind-texture [] (bind-texture 0))
(defn- set-active-texture-slot [slot] (GL33/glActiveTexture (+ GL33/GL_TEXTURE0 slot)))
(defn- free-image-buffer [^ByteBuffer img-buffer] (STBImage/stbi_image_free img-buffer))

(defprotocol ITexture
  (^:private set-slot-of-a-texture [texture slot] "Sets the current slot for this texture.")
  (get-texture-slot [texture] "Returns current mutable value of the texture slot of this texture.")
  (bind-texture-to-slot [texture slot] "Binds this texture to a slot (and unbinds others from it if any are bound).")
  (unbind-texture-from-current-slot [texture] "Unbinds a texture from its current slot."))

(defn get-texture-by-slot [slot] (aget textures-by-slot slot))
(defn set-texture-slot-array [slot texture] (aset textures-by-slot slot texture))

(deftype Texture [^Integer texture-id width height texture-lookup-id ^:unsynchronized-mutable texture-slot]
  ITexture
  (set-slot-of-a-texture [this new-slot] (set! texture-slot new-slot))
  (get-texture-slot [this] texture-slot)
  (unbind-texture-from-current-slot [this]
    (unbind-texture)
    (when texture-slot (set-active-texture-slot texture-slot))
    (set-slot-of-a-texture this -1))
  (bind-texture-to-slot [this slot]
    (when-let [old-texture-on-this-slot (get-texture-by-slot slot)]
      (set-slot-of-a-texture old-texture-on-this-slot -1))
    (set-active-texture-slot slot)
    (bind-texture texture-id)
    (set-texture-slot-array slot this)
    (set-slot-of-a-texture this slot))
  IDisposable
  (disposer/dispose [this]
    (GL33/glDeleteTextures ^Integer texture-id)
    (unbind-texture-from-current-slot this)
    (swap! textures-by-lookup-id dissoc texture-lookup-id)))

(defn clear-texture-slot [slot]
  (when-let [texture (get-texture-by-slot slot)]
    (set-slot-of-a-texture texture -1))
  (set-texture-slot-array slot nil))

(defn delete-textures []
  (doseq [texture (vals @textures-by-lookup-id)]
    (disposer/dispose texture)))

(defn setup-needed-parameters []
  (doseq [[param val] [[GL33/GL_TEXTURE_MIN_FILTER GL33/GL_LINEAR]
                       [GL33/GL_TEXTURE_MAG_FILTER GL33/GL_LINEAR]
                       [GL33/GL_TEXTURE_WRAP_S GL33/GL_CLAMP]
                       [GL33/GL_TEXTURE_WRAP_T GL33/GL_CLAMP]]]
    (GL33/glTexParameteri tex-2d param val)))

(defn load-texture [^String src-path]
  (let [[^ints width ^ints height ^ints channels-in-file] (repeatedly #(int-array 1))
        _ (STBImage/stbi_set_flip_vertically_on_load true)
        ^ByteBuffer img-buffer (STBImage/stbi_load src-path width height channels-in-file 4)
        [width height channels-in-file] (map #(aget % 0) [width height channels-in-file])]
    [img-buffer width height channels-in-file]))

(defn texture [src-path texture-lookup-id & {:keys [texture-slot]}]
  (when-not (fs/exists? src-path)
    (throw (FileNotFoundException. (str "Texture path not found: " src-path))))
  (let [texture-id (doto (gen-texture) bind-texture)
        [^ByteBuffer img-buffer ^int width ^int height ^int channels-in-file] (load-texture src-path)
        _ (debug/assert-all (setup-needed-parameters)
                            (GL33/glTexImage2D tex-2d, 0, GL33/GL_RGBA8, width, height, 0, GL33/GL_RGBA, gl/U-BYTE, img-buffer)
                            (unbind-texture)
                            (some-> img-buffer free-image-buffer))
        texture (Texture. texture-id width height texture-lookup-id texture-slot)]
    (debug/assert-all (swap! textures-by-lookup-id assoc texture-lookup-id texture)
                      (when texture-slot
                        (bind-texture-to-slot texture texture-slot)))))