(ns internet-explorer.cljs.views.blog
  (:require [internet-explorer.cljc.routes :as routes]
            [internet-explorer.cljs.views.components :as c]
            [internet-explorer.cljs.models.blog :as b]))

(defn blog []
      [:div
       [:h1 "internet-explorer: Blog"]
       (if @b/all-entries
         [:div (for [{:keys [id title]} @b/all-entries]
                    ^{:key id}
                    [:div
                     [:a {:href (routes/page :blog-entry :id id)} title]])]
         [c/loading-spinner])])

(defn blog-entry []
      (let [{:keys [title content]} @b/blog-entry]
           [:div
            [:h1 (or title [c/loading-spinner])]
            [:div
             [:p (or content "")]
             [:a {:href (routes/page :blog)} "Back to Blog"]]]))
