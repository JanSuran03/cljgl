(ns cljgl.common.destructor)

(defprotocol IDestructor
  (destroy [this] "Calls a destruction on this object."))