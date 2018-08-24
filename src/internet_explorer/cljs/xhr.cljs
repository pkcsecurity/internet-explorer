(ns internet-explorer.cljs.xhr
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [reagent.core :as r]
            [reagent.cookies :as cookies]
            [cljs.core.async :as async :refer [<!]]
            [internet-explorer.cljc.routes :as routes]))

(defn auth-header []
      (if-let [token (cookies/get :brevity-token)]
              {"Authorization" (str "Token " token)}
              {}))

(defn simple-xhr [method url & {:keys [data on-success on-error success-atom error-atom query-params]}]
      ; TODO don't set the auth header on external requests
      (go
        (let [request (http/request
                        {:method method
                         :url url
                         :json-params data
                         :query-params query-params
                         :headers (auth-header)})
              response (<! request)
              {:keys [status body]} response]
             (if (<= 200 status 299)
               (do
                 (when on-success (on-success response))
                 (when success-atom (reset! success-atom body)))
               (do
                 (when on-error (on-error response))
                 (when error-atom (reset! error-atom body)))))))

(def send-get (partial simple-xhr :get))

(def send-post (partial simple-xhr :post))

(def send-put (partial simple-xhr :put))

(def send-delete (partial simple-xhr :delete))
