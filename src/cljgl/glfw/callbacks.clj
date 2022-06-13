(ns cljgl.glfw.callbacks
  "All callbacks have to be in format e.g.
  (set-key-callback @window* (fn [window-arg key scancode action mode]
                               (println window-arg key scancode action mode))"
  (:import (org.lwjgl.glfw GLFW GLFWFramebufferSizeCallback GLFWMouseButtonCallback GLFWKeyCallback GLFWScrollCallback GLFWCursorEnterCallback GLFWCursorPosCallback)))

(defmacro set-key-callback [window-ref [_fn [window-arg key scancode action mode] & body]]
  `(GLFW/glfwSetKeyCallback ~window-ref
                            (proxy [GLFWKeyCallback] []
                              (invoke [~window-arg ~key ~scancode ~action ~mode] ~@body))))

(defmacro set-framebuffer-size-callback [window-ref [_fn [window-arg width height] & body]]
  `(GLFW/glfwSetFramebufferSizeCallback ~window-ref
                                        (proxy [GLFWFramebufferSizeCallback] []
                                          (invoke [~window-arg ~width ~height] ~@body))))

(defmacro set-mouse-callback [window-ref [_fn [window-arg button action mods] & body]]
  `(GLFW/glfwSetMouseButtonCallback ~window-ref
                                    (proxy [GLFWMouseButtonCallback] []
                                      (invoke [~window-arg ~button ~action ~mods] ~@body))))

(defmacro set-scroll-callback [window-ref [_fn [window-arg x-offset y-offset] & body]]
  `(GLFW/glfwSetScrollCallback ~window-ref
                               (proxy [GLFWScrollCallback] []
                                 (invoke [~window-arg ~x-offset ~y-offset] ~@body))))

(defmacro set-cursor-enter-callback [window-ref [_fn [window-arg entered?] & body]]
  `(GLFW/glfwSetCursorEnterCallback ~window-ref
                                    (proxy [GLFWCursorEnterCallback] []
                                      (invoke [~window-arg ~entered?] ~@body))))

(defmacro set-cursor-pos-callback [window-ref [_fn [window-arg x-pos y-pos] & body]]
  `(GLFW/glfwSetCursorPosCallback ~window-ref
                                  (proxy [GLFWCursorPosCallback] []
                                    (invoke [~window-arg ~x-pos ~y-pos] ~@body))))