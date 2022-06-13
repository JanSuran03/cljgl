(require '[leiningen.core.eval :as eval])

(def JVM-OPTS {:common  []
               :macosx  ["-XstartOnFirstThread" "-Djava.awt.headless=true"]
               :linux   []
               :windows []})

(defn jvm-opts
  "Returns a complete vector of jvm-opts for the current os."
  [] (let [os (eval/get-os)]
       (vec (set (concat (get JVM-OPTS :common)
                   (get JVM-OPTS os))))))

(def lwjgl-ns "org.lwjgl")
(def lwjgl-version "3.3.1")
(def lwjgl-modules ["lwjgl"
                    "lwjgl-assimp"
                    "lwjgl-bgfx"
                    "lwjgl-egl"
                    "lwjgl-glfw"
                    "lwjgl-jawt"
                    "lwjgl-jemalloc"
                    "lwjgl-lmdb"
                    "lwjgl-lz4"
                    "lwjgl-nanovg"
                    "lwjgl-nfd"
                    "lwjgl-nuklear"
                    "lwjgl-odbc"
                    "lwjgl-openal"
                    "lwjgl-opencl"
                    "lwjgl-opengl"
                    "lwjgl-opengles"
                    "lwjgl-openvr"
                    "lwjgl-par"
                    "lwjgl-remotery"
                    "lwjgl-rpmalloc"
                    "lwjgl-sse"
                    "lwjgl-stb"
                    "lwjgl-tinyexr"
                    "lwjgl-tinyfd"
                    "lwjgl-tootle"
                    "lwjgl-vulkan"
                    "lwjgl-xxhash"
                    "lwjgl-yoga"
                    "lwjgl-zstd"])

(def lwjgl-platforms ["linux" "macos" "windows"])
;; These packages don't have any associated native ones.
(def no-natives? #{"lwjgl-egl" "lwjgl-jawt" "lwjgl-odbc" "lwjgl-opencl" "lwjgl-vulkan"})

(defn lwjgl-deps-with-natives []
  (mapcat (fn [m]
            (let [prefix [(symbol lwjgl-ns m) lwjgl-version]]
              (into [prefix]
                (when-not (no-natives? m)
                  (for [p lwjgl-platforms]
                    (into prefix [:classifier (str "natives-" p)
                                  :native-prefix ""]))))))
    lwjgl-modules))

(def all-dependencies
  (into '[[me.raynes/fs "1.4.6"]
          [org.clojure/clojure "1.10.1"]
          [org.clojure/core.async "1.5.648"]]
    (lwjgl-deps-with-natives)))

(defproject org.clojars.jansuran03/cljgl "0.1.0"
  :dependencies ~all-dependencies
  :java-source-paths ["src/java"]
  :jvm-opts ^:replace ~(jvm-opts)
  :repl-options {:init-ns cljgl.core})
