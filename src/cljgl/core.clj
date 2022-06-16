(ns cljgl.core
  (:refer-clojure :exclude [compile]))

(defonce version "0.1.0")

(defn -main [& args]
  (println (str "Hello, cljgl " version "!")))

(defmacro compile []
  (let [all-ns '[[cljgl.common [cleanup] [colors] [debug] [disposer] [gl-util]]
                 [cljgl.glfw [callbacks] [glfw] [keys]]
                 [cljgl.math [math] [matrix4f]]
                 [cljgl.opengl [buffers] [gl] [renderer] [shaders] [textures]]]]
    `(binding [clojure.core/*loading-verbosely* true]
       (require
         ~@(for [[prepend & nss] all-ns
                 [ns] nss]
             (list 'quote [(symbol (str (name prepend) "." (name ns))) :reload true]))))))