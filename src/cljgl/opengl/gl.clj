(ns cljgl.opengl.gl
  (:import (org.lwjgl Version)
           (org.lwjgl.opengl GL GL33)))

;; ------------------------------------------------------
;; GL
;; ------------------------------------------------------
(defn create-capabilities [] (GL/createCapabilities))
(defn get-version [] (Version/getVersion))
;; ------------------------------------------------------
;; consts
;; ------------------------------------------------------
(defonce ^{:doc "GL33/GL_TRUE"} T GL33/GL_TRUE)
(defonce ^{:doc "GL33/GL_FALSE"} F GL33/GL_FALSE)
;; ------------------------------------------------------
;; GL DATA TYPES
;; ------------------------------------------------------
(defonce ^Integer INT GL33/GL_INT)
(defonce ^Integer UNSIGNED-INT GL33/GL_UNSIGNED_INT)
(defonce ^Integer BYTE GL33/GL_BYTE)
(defonce ^Integer U-BYTE GL33/GL_UNSIGNED_BYTE)
(defonce ^Integer SHORT GL33/GL_SHORT)
(defonce ^Integer U-SHORT GL33/GL_UNSIGNED_SHORT)
(defonce ^Integer DOUBLE GL33/GL_DOUBLE)
(defonce ^Integer FLOAT GL33/GL_FLOAT)
(defonce ^Integer HALF-FLOAT GL33/GL_HALF_FLOAT)
;; ------------------------------------------------------
;; GL33
;; ------------------------------------------------------
(defonce ^Integer triangles GL33/GL_TRIANGLES)
(defonce ^Integer color-buffer-bit GL33/GL_COLOR_BUFFER_BIT)
(defn clear-color [r g b a] (GL33/glClearColor r g b a))
(defn clear-bits [& bits] (GL33/glClear (reduce bit-or bits)))
;; ------------------------------------------------------
;; vertex attributes
;; ------------------------------------------------------
(defn setup-vertex-attribute
  "attribute-location - the location specified in the shader code
                        via layout (location = <location>) ...
  count - the number of components per vertex attribute (e.g. 3 for vec3f)
  data-type - the data type for the vertex attribute components
  normalize? - whether the data should be normalized (converted to fit
               in the interval <-1; 1>
  vertex-size - size of the vertex data in bytes (so that all the parallel GPU cores can
                skip to current vertex data as if it were in an array of the same type)
  byte-offset - byte offset of the attribute"
  [^Integer attribute-location
   ^Integer count
   ^Integer data-type
   ^Boolean normalize?
   ^Integer vertex-size
   ^Long byte-offset])

(defn disable-vertex-attrib-array [index] (GL33/glEnableVertexAttribArray index))
(defn enable-vertex-attrib-array [index] (GL33/glDisableVertexAttribArray index))
;; ------------------------------------------------------
;; view stuff
;; ------------------------------------------------------
(defn viewport [x y width height] (GL33/glViewport x y width height))