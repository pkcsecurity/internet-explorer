(ns internet-explorer.cljs.controllers.blog
  (:require [internet-explorer.cljc.routes :as routes]
            [internet-explorer.cljs.xhr :as xhr]
            [internet-explorer.cljs.models.blog :as m]))

(defn blog-entries []
      (xhr/send-get
        (routes/api :blog)
        :success-atom m/all-entries))

(defn blog-entry [{:keys [id]}]
      (reset! m/blog-entry nil)
      (xhr/send-get
        (routes/api :blog-entry :id id)
        :success-atom m/blog-entry))
