(ns cljgl.glfw.keys
  (:refer-clojure :exclude [repeat])
  (:import (org.lwjgl.glfw GLFW)))

;; ---------------------- general ----------------------
(defonce ^Integer press GLFW/GLFW_RELEASE)
(defonce ^Integer release GLFW/GLFW_RELEASE)
(defonce ^Integer repeat GLFW/GLFW_REPEAT)

;; ---------------------- key ----------------------
(defonce ^Integer esc GLFW/GLFW_KEY_ESCAPE)
(defonce ^Integer space GLFW/GLFW_KEY_SPACE)
(defonce ^Integer up GLFW/GLFW_KEY_UP)
(defonce ^Integer down GLFW/GLFW_KEY_DOWN)
(defonce ^Integer left GLFW/GLFW_KEY_LEFT)
(defonce ^Integer right GLFW/GLFW_KEY_RIGHT)

;; ---------------------- mouse ----------------------
(defonce ^Integer mouse-left GLFW/GLFW_MOUSE_BUTTON_1)
(defonce ^Integer mouse-right GLFW/GLFW_MOUSE_BUTTON_2)
(defonce ^Integer mouse-center GLFW/GLFW_MOUSE_BUTTON_3)
(defonce ^Integer mouse-backward GLFW/GLFW_MOUSE_BUTTON_4)
(defonce ^Integer mouse-forward GLFW/GLFW_MOUSE_BUTTON_5)
(defonce ^Integer mouse-forward GLFW/GLFW_MOUSE_BUTTON_5)


(defn ctrl? [mods-integer] (> mods-integer (bit-xor mods-integer GLFW/GLFW_MOD_CONTROL)))
(defn alt? [mods-integer] (> mods-integer (bit-xor mods-integer GLFW/GLFW_MOD_ALT)))
(defn shift? [mods-integer] (> mods-integer (bit-xor mods-integer GLFW/GLFW_MOD_SHIFT)))
