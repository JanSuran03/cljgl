(ns cljgl.glfw.keys
  (:import (org.lwjgl.glfw GLFW)))

(defonce ^Integer release GLFW/GLFW_RELEASE)
(defonce ^Integer hold GLFW/GLFW_REPEAT)
(defonce ^Integer esc GLFW/GLFW_KEY_ESCAPE)
(defonce ^Integer space GLFW/GLFW_KEY_SPACE)
(defonce ^Integer up GLFW/GLFW_KEY_UP)
(defonce ^Integer down GLFW/GLFW_KEY_DOWN)
(defonce ^Integer left GLFW/GLFW_KEY_LEFT)
(defonce ^Integer right GLFW/GLFW_KEY_RIGHT)
(defn ctrl? [mods-integer] (> mods-integer (bit-xor mods-integer GLFW/GLFW_MOD_CONTROL)))
(defn alt? [mods-integer] (> mods-integer (bit-xor mods-integer GLFW/GLFW_MOD_ALT)))
(defn shift? [mods-integer] (> mods-integer (bit-xor mods-integer GLFW/GLFW_MOD_SHIFT)))