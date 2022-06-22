(ns cljgl.math.matrix4f
  (:import (cljgl Matrix4f)))

(defn orthogonal [left right bottom top near far]
  (Matrix4f/orthogonal left right bottom top near far))

(list :model-matrix :view-matrix :projection-matrix)

(defn model-view-projection-matrix [{:keys [model-matrix view-matrix projection-matrix scale]
                                     #_:or #_{model-matrix      (Matrix4f/identity)
                                              view-matrix       (Matrix4f/identity)
                                              projection-matrix (Matrix4f/identity)}}]
  (let [scale-matrix (when scale (Matrix4f/scale (nth scale 0) (nth scale 1) (nth scale 2)))]
    #_(-> projection-matrix
          (Matrix4f/multiply view-matrix)
          (Matrix4f/multiply model-matrix)
          (Matrix4f/multiply scale-matrix))
    (cond-> projection-matrix
            view-matrix (Matrix4f/multiply view-matrix)
            model-matrix (Matrix4f/multiply model-matrix)
            scale-matrix (Matrix4f/multiply scale-matrix))))