(ns cljgl.math.matrix4f
  (:import (cljgl Matrix4f)))

(defn orthogonal [left right bottom top near far]
  (Matrix4f/orthogonal left right bottom top near far))

(list :model-matrix :view-matrix :projection-matrix)

(defn model-view-projection-matrix [{:keys [model-matrix view-matrix projection-matrix scale]
                                     :or   {model-matrix      (Matrix4f/identity)
                                            view-matrix       (Matrix4f/identity)
                                            projection-matrix (Matrix4f/identity)}}]
  (let [[scale-x scale-y scale-z] (if scale scale [1 1 1])
        scale-matrix (Matrix4f/scale scale-x scale-y scale-z)]
    (-> projection-matrix
        (Matrix4f/multiply view-matrix)
        (Matrix4f/multiply model-matrix)
        (Matrix4f/multiply scale-matrix))))