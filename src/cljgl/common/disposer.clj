(ns cljgl.common.disposer)

(defprotocol IDisposable
  (dispose [this] "Calls a destruction on an object and releases GPU memory associated with it.
  Most of the implementations are recursive."))