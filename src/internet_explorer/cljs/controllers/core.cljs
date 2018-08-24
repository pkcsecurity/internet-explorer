(ns internet-explorer.cljs.controllers.core
  (:require [internet-explorer.cljs.controllers.blog :as blog]))

(def page-initializers
  {:blog-entry blog/blog-entry
   :blog blog/blog-entries})
