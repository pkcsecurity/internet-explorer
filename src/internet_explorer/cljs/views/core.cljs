(ns internet-explorer.cljs.views.core
  (:require [internet-explorer.cljc.routes :as routes]
            [internet-explorer.cljs.views.blog :as blog]
            [internet-explorer.cljs.views.account :as account]
            [internet-explorer.cljs.views.components :as c]
            [reagent.session :as session]))

(defn index [params]
      [:div
       [:h1 "internet-explorer"]
       [:p
        "Brevity's default styles are pretty basic.  To tailor them to your project, see "
        [:a {:href "https://tachyons.io/docs/"} "the tachyons documentation"] "."]])

(defn four-o-four [params]
      [:div
       [:h1 "404: Page not found"]
       [:p ":("]])

(def views
  {:index index
   :login account/login
   :four-o-four four-o-four
   :blog blog/blog
   :blog-entry blog/blog-entry})

(defn page-contents [route]
      (let [page (:current-page route)
            params (:route-params route)]
           [:div.mw7.pv3.ph5.center
            [(views page) params]]))

(defn layout []
      (fn []
          (let [route (session/get :route)]
               [:div
                [c/header]
                ^{:key route} [page-contents route]
                [c/footer]])))
