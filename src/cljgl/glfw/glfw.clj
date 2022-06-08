(ns cljgl.glfw.glfw
  (:require [cljgl.common.gl-util :refer [null]])
  (:import (org.lwjgl.glfw Callbacks GLFW GLFWErrorCallback GLFWFramebufferSizeCallback
                           GLFWKeyCallback GLFWVidMode)
           (org.lwjgl.system MemoryStack)
           (java.nio IntBuffer)))
;; ------------------------------------------------------
;; common stuff
;; ------------------------------------------------------
(defn bool [x] (if x GLFW/GLFW_TRUE GLFW/GLFW_FALSE))
(defn init [] (GLFW/glfwInit))
(defn terminate [] (GLFW/glfwTerminate))
;; ------------------------------------------------------
;; rendering stuff
;; ------------------------------------------------------
(defn vsync [] (GLFW/glfwSwapInterval 1))
(defn swap-buffers [window] (GLFW/glfwSwapBuffers window))
(defn poll-events [] (GLFW/glfwPollEvents))
;; ------------------------------------------------------
;; callbacks stuff
;; ------------------------------------------------------
(defn enable-error-callback-print [] (.set (GLFWErrorCallback/createPrint System/out)))
(defn unbind-error-callback [] (.free (GLFW/glfwSetErrorCallback nil)))
(defn free-callbacks [window] (Callbacks/glfwFreeCallbacks window))
(defn make-context-current [window] (GLFW/glfwMakeContextCurrent window))

(defmacro set-key-callback
  "Has to be in format
  (set-key-callback (fn [window-arg key scancode action mode]
                      (println window-arg key scancode action mode))"
  [window-reference [_fn [window-arg key scancode action mode] & body]]
  `(GLFW/glfwSetKeyCallback ~window-reference
                            (proxy [GLFWKeyCallback] []
                              (invoke [~window-arg ~key ~scancode ~action ~mode] ~@body))))

(defmacro set-framebuffer-size-callback
  "Has to be in format
  (set-framebuffer-size-callback (fn [window-arg width height]
                                   (println window-arg width height]))"
  [window-reference [_fn [window-arg width height] & body]]
  `(GLFW/glfwSetFramebufferSizeCallback ~window-reference
                                        (proxy [GLFWFramebufferSizeCallback] []
                                          (invoke [~window-arg ~width ~height] ~@body))))
;; ------------------------------------------------------
;; window and monitor stuff
;; ------------------------------------------------------
(defn get-primary-monitor [] (GLFW/glfwGetPrimaryMonitor))
(defn get-video-mode [] (GLFW/glfwGetVideoMode (get-primary-monitor)))
(defn show-window [window] (GLFW/glfwShowWindow window))
(defn destroy-window [window] (GLFW/glfwDestroyWindow window))
(defn close-window ([window] (GLFW/glfwSetWindowShouldClose window true)))
(defn should-window-close? [window] (GLFW/glfwWindowShouldClose window))
(defn set-window-pos [window x y] (GLFW/glfwSetWindowPos window x y))

(defn get-monitor-size
  "[width height]"
  [] (let [^GLFWVidMode vid-mode (get-video-mode)]
       [(.width vid-mode) (.height vid-mode)]))

(defn window-resolutions
  "[width height]"
  [window] (let [^MemoryStack stack (MemoryStack/stackPush)
                 ^IntBuffer p-width (.mallocInt stack 1)
                 ^IntBuffer p-height (.mallocInt stack 1)]
             (GLFW/glfwGetWindowSize ^Long window p-width p-height)
             [(.get p-width) (.get p-height)]))

(defn create-window
  ([width height title] (create-window width height title null null))
  ([^Integer width ^Integer height ^String title ^Long monitor ^Long share]
   (GLFW/glfwCreateWindow width height title monitor share)))

(defn center-window [window]
  (let [[window-width window-height] (window-resolutions window)
        [monitor-width monitor-height] (get-monitor-size)]
    (set-window-pos window (quot (- monitor-width window-width) 2)
                    (quot (- monitor-height window-height) 2))))
;; ------------------------------------------------------
;; window hints stuff
;; ------------------------------------------------------
(defn default-window-hints [] (GLFW/glfwDefaultWindowHints))
(defn window-hint-visible [?] (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE (bool ?)))
(defn window-hint-resizable [?] (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE (bool ?)))