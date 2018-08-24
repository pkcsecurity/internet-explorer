(ns internet-explorer.clj.routes.scrape
  (:require [internet-explorer.clj.okhttp :as okhttp]
            [hickory.core :as h]
            [hickory.select :as hs]
            [hickory.hiccup-utils :as hic-utils]
            [hickory.render :as rend])
  (:import [org.jsoup Jsoup]))

(defn get-html [{{:keys [url]} :body}]
  (let [res (okhttp/sync-req "GET" url :response-type :text)
        parsed (-> res
                   (h/parse)
                   (h/as-hickory))
        all-links (->> (hs/select (hs/tag :a) parsed)
                       (filter (comp string? first :content))
                       (shuffle)
                       (take 5)
                       (map (fn [{:keys [attrs content]}]
                              {:href (:href attrs)
                               :content (first content)})))
        all-content (->> (hs/select (hs/tag :p) parsed)
                         (map (comp #(.text %) #(Jsoup/parse %) rend/hickory-to-html)))]
    {:body {:urls all-links
            :content all-content}
     :status 200}))
