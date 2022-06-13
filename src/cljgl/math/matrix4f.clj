(ns cljgl.math.matrix4f
  (:import (cljgl Matrix4f)))

(defn orthogonal [left right bottom top near far]
  (Matrix4f/orthogonal left right bottom top near far))

(list :model-matrix :view-matrix :projection-matrix)

(defn model-view-projection-matrix [{:keys [model-matrix view-matrix projection-matrix] :or
                                     {model-matrix      (Matrix4f/identity)
                                      view-matrix       (Matrix4f/identity)
                                      projection-matrix (Matrix4f/identity)}}]
  (-> projection-matrix (Matrix4f/multiply view-matrix) (Matrix4f/multiply model-matrix)))