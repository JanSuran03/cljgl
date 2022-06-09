(ns cljgl.opengl.shaders
  (:require [cljgl.common.gl-util :as gl-util]
            [cljgl.common.disposer :as disposer]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (cljgl.common.disposer IDisposable)
           (clojure.lang PersistentHashMap)
           (java.io FileNotFoundException)
           (org.lwjgl.opengl GL33)))

(def current-shader-program (atom -1))

(defn- use-shader-program-impl [shader-program]
  (when-not (= shader-program @current-shader-program)
    (reset! current-shader-program shader-program)
    (GL33/glUseProgram shader-program)))

(defn unbind-shader-program [] (use-shader-program-impl 0))

;; ------------------------------------------------------
;; shaders
;; ------------------------------------------------------
(defn- read-shaders [shader-source-path]
  (let [lines (try (line-seq (io/reader shader-source-path))
                   (catch FileNotFoundException e
                     (.printStackTrace e)
                     (throw (FileNotFoundException.
                              (str "SHADER SOURCE NOT FOUND:" shader-source-path)))))]
    (loop [mode nil
           [line & more] lines
           split-code {:vertex-source (StringBuilder.), :fragment-source (StringBuilder.)}]
      (cond (nil? line) (into {} (map (fn [[k v]] [k (str v)]) split-code))
            (str/starts-with? line ";;VERT") (recur :vertex-source more split-code)
            (str/starts-with? line ";;FRAG") (recur :fragment-source more split-code)
            (nil? mode) (throw (RuntimeException. "Shader type not specified."))
            :else (recur mode more (update split-code mode #(doto ^StringBuilder % (.append \newline)
                                                                                   (.append line))))))))

(defn- create-shader [shader-type] (GL33/glCreateShader shader-type))
(defn- attach-shader-source [^Integer shader ^String shader-source] (GL33/glShaderSource shader shader-source))
(defn- compile-shader [shader] (GL33/glCompileShader shader))

(defn- get-shader-status [^Integer shader ^Integer parameter-name]
  (let [result-buffer (int-array 1)]
    (GL33/glGetShaderiv shader parameter-name result-buffer)
    (aget result-buffer 0)))

(defn- shader-compiled? [shader] (gl-util/ok? (get-shader-status shader GL33/GL_COMPILE_STATUS)))

(defn- create-and-compile-shader [shader-type shader-source]
  (doto (create-shader shader-type)
    (attach-shader-source shader-source)
    compile-shader))

(defn- ^String get-shader-info-log [shader] (GL33/glGetShaderInfoLog shader))
(defn- delete-shader [shader] (GL33/glDeleteShader shader))

(defn- create-and-compile-vertex-shader [vertex-source debug-src-path]
  (let [compiled-shader (create-and-compile-shader GL33/GL_VERTEX_SHADER vertex-source)]
    (if (shader-compiled? compiled-shader)
      compiled-shader
      (do (delete-shader compiled-shader)
          (gl-util/log (str "Vertex shader compilation failed! Source path: " debug-src-path))
          (throw (RuntimeException. (get-shader-info-log compiled-shader)))))))

(defn- create-and-compile-fragment-shader [fragment-source debug-src-path]
  (let [compiled-shader (create-and-compile-shader GL33/GL_FRAGMENT_SHADER fragment-source)]
    (if (shader-compiled? compiled-shader)
      compiled-shader
      (do (delete-shader compiled-shader)
          (gl-util/log (str "Fragment shader compilation failed! Source path: " debug-src-path))
          (throw (RuntimeException. (get-shader-info-log compiled-shader)))))))
;; ------------------------------------------------------
;; shader program
;; ------------------------------------------------------
(defn- create-shader-program [] (GL33/glCreateProgram))
(defn- attach-shader-to-shader-program [shader-program compiled-shader] (GL33/glAttachShader shader-program compiled-shader))
(defn- detach-shader-from-shader-program [shader-program compiled-shader] (GL33/glDetachShader shader-program compiled-shader))
(defn- link-shader-program [shader-program] (GL33/glLinkProgram shader-program))
(defn- get-shader-program-status [^Integer shader-program ^Integer parameter-name] (GL33/glGetProgrami shader-program parameter-name))
(defn- get-shader-program-info-log [shader-program] (GL33/glGetProgramInfoLog shader-program))

(defn- assert-shader-program-linked [shader-program]
  (when-not (gl-util/ok? (get-shader-program-status shader-program GL33/GL_LINK_STATUS))
    (throw (RuntimeException. (str "Shader program linking failed: "
                                   (get-shader-program-info-log shader-program))))))
(defn- validate-shader-program [shader-program] (gl-util/ok? (GL33/glValidateProgram shader-program)))
(defn assert-shader-program-validated [shader-program]
  (when-not (gl-util/ok? (get-shader-program-status shader-program GL33/GL_VALIDATE_STATUS))
    (throw (RuntimeException. (str "Shader program validation failed: "
                                   (get-shader-program-info-log shader-program))))))

(defprotocol IShaderProgram
  (set-uniform-4f [this uniform-name v1 v2 v3 v4] "Sets the value of a vec4 uniform bound to the shader program.")
  (set-uniform-1f [this uniform-name float-val] "Sets the value of a float uniform bound to the shader program.")
  (set-uniform-1i [this uniform-name int-val] "Sets the value of an integer uniform bound to the shader program.")
  (set-uniform-mat4f [this uniform-name mat4f] "Sets the value of an matrix4f uniform bound to the shader program.")
  (use-shader-program [this] "Sets the shader program as the currently used one."))

(defprotocol IMutableUniformsFieldAccess
  (cache-found-uniform [this uniform-name uniform-location] "Caches a found uniform.")
  (get-uniform-location [this uniform-name] "Returns the location of a uniform bound to the shader program.")
  (get-uniforms [this] "Returns a mutable reference to a Clojure hash-map with cached uniforms."))

(defonce ^:private shader-programs (atom {}))

(defn- delete-shader-program [shader-program-lookup-id]
  (GL33/glDeleteProgram shader-program-lookup-id)
  (swap! shader-programs dissoc shader-program-lookup-id))

(defn delete-shader-programs []
  (doseq [[_ shader-program] @shader-programs]
    (disposer/dispose shader-program))
  (reset! shader-programs {}))

(deftype ShaderProgram [^Integer shader-program-id ^:unsynchronized-mutable ^PersistentHashMap uniforms]
  IMutableUniformsFieldAccess
  (cache-found-uniform [_this uniform-name uniform-location]
    (set! uniforms (assoc uniforms uniform-name uniform-location)))
  (get-uniforms [_this] uniforms)
  (get-uniform-location [this uniform-name]
    (if-let [uniform-location (get uniforms uniform-name)]
      uniform-location
      (let [shader-program-id shader-program-id
            uniform-location (GL33/glGetUniformLocation ^Integer shader-program-id ^String uniform-name)]
        (if (= uniform-location -1)
          (println (str "Uniform <" uniform-name "> not found for shader program: " shader-program-id))
          (do (cache-found-uniform this uniform-name uniform-location)
              uniform-location)))))
  IShaderProgram
  (set-uniform-4f [this uniform-name v1 v2 v3 v4]
    (use-shader-program-impl shader-program-id)
    (if-let [uniform-location (get-uniform-location this uniform-name)]
      (GL33/glUniform4f uniform-location v1 v2 v3 v4)
      (throw (RuntimeException. (str "Can't set 4f uniform <" uniform-name "> for shader "
                                     shader-program-id ": Uniform not found.")))))
  (set-uniform-1i [this uniform-name int-val]
    (use-shader-program-impl shader-program-id)
    (if-let [uniform-location (get-uniform-location this uniform-name)]
      (GL33/glUniform1i uniform-location int-val)
      (throw (RuntimeException. (str "Can't set 1i uniform <" uniform-name "> for shader "
                                     shader-program-id ": Uniform not found.")))))

  (set-uniform-1f [this uniform-name int-val]
    (use-shader-program-impl shader-program-id)
    (if-let [uniform-location (get-uniform-location this uniform-name)]
      (GL33/glUniform1f uniform-location int-val)
      (throw (RuntimeException. (str "Can't set 1f uniform <" uniform-name "> for shader "
                                     shader-program-id ": Uniform not found.")))))

  (set-uniform-mat4f [this uniform-name mat4f]
    (let [shader-program shader-program-id]
      (use-shader-program this)
      (if-let [uniform-location (get-uniform-location this uniform-name)]
        (GL33/glUniformMatrix4fv ^Integer uniform-location false ^"[F" mat4f)
        (throw (RuntimeException. (str "Can't set 4f uniform <" uniform-name "> for shader program ("
                                       shader-program "): Uniform not found."))))))
  (use-shader-program [_this] (use-shader-program-impl shader-program-id))
  IDisposable
  (disposer/dispose [this]
    (delete-shader-program shader-program-id)))

(defn make-shader-program [shader-program-id src-path]
  (let [{:keys [vertex-source fragment-source]} (read-shaders src-path)
        vertex-shader (create-and-compile-vertex-shader vertex-source src-path)
        fragment-shader (create-and-compile-fragment-shader fragment-source src-path)
        shader-program (doto (create-shader-program)
                         (attach-shader-to-shader-program vertex-shader)
                         (attach-shader-to-shader-program fragment-shader)
                         link-shader-program
                         assert-shader-program-linked
                         validate-shader-program
                         assert-shader-program-validated)
        _ (do (delete-shader vertex-shader)
              (delete-shader fragment-shader)
              (detach-shader-from-shader-program shader-program vertex-shader)
              (detach-shader-from-shader-program shader-program fragment-shader))
        shader-program-obj (ShaderProgram. shader-program {})]
    (swap! shader-programs assoc shader-program-id shader-program-obj)
    shader-program-obj))