(ns internet-explorer.clj.views.core
    (:require [environ.core :as environ]
              [hiccup.core :as html]
              [garden.core :as css]))

(def core-css
  (css/css
    {:pretty-print? false}
    [:* {:box-sizing :border-box}]
    [:body {:font-size "16px"
            :line-height 1.5}]
    [:html :body
     {:color "#222"
      :font-weight 700}]))

(defn style [href & {:keys [integrity]}]
      [:link
       {:rel "stylesheet"
        :href href
        :integrity integrity
        :crossorigin :anonymous}])

(def index
  (html/html {:mode :html}
             [:head
              [:meta {:charset "utf-8"}]
              [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
              [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
              (style "/css/tachyons.min.css")
              (style "https://use.fontawesome.com/releases/v5.1.1/css/all.css"
                     :integrity "sha384-O8whS3fhG2OnA5Kas0Y9l3cfpmYjapjI0E4theH4iuMD+pLhbf6JI0jIMfYcK3yZ")
              [:title "internet-explorer"]]
             [:body.avenir
              [:div#app]
              [:script {:src (if (= "development" (environ/env :environment)) "/js/development/index.js" "/js/release/index.js")}]]))

