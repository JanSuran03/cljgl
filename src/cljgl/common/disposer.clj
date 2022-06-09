(ns cljgl.common.disposer)

(defprotocol IDisposable
  (dispose [this] "Calls a destruction on this object."))