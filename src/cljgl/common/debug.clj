(ns cljgl.common.debug
  (:refer-clojure :exclude [assert])
  (:import (org.lwjgl.opengl GL33)))

(defonce no-error GL33/GL_NO_ERROR)

(defn get-error
  "Returns an integer value of an OpenGL error type."
  []
  (GL33/glGetError))

(defn clear-errors []
  (while (not= (get-error) no-error)))

(defn get-errors []
  (loop [error (get-error)
         errors []]
    (if (= error no-error)
      errors
      (recur (get-error)
             (conj errors error)))))

(defonce ^:private __enable-assert?* (atom true))

(defmacro assert [form]
  (let [{:keys [line column] :or {line :undefined column :undefined}} (meta &form)]
    (if @__enable-assert?*
      `(do (clear-errors)
           (let [ret# ~form
                 errors# (get-errors)]
             (if (seq errors#)
               (throw (RuntimeException. (str "OpenGL error(s): " errors# " at line " ~line
                                              ", column: " ~column ", function: " ~(str form))))
               ret#)))
      form)))
(defmacro assert-all [& forms]
  `(do ~@(map #(list `assert %) forms)))

(defn disable-assert! [] (clear-errors) (reset! __enable-assert?* false))
(defn enable-assert! [] (clear-errors) (reset! __enable-assert?* true))
