(ns internet-explorer.cljs.controllers.session
  (:require [internet-explorer.cljc.routes :as routes]
            [internet-explorer.cljs.xhr :as xhr]
            [internet-explorer.cljs.models.session :as s]
            [reagent.cookies :as cookies]
            [accountant.core :as accountant]))


(defn successful-login [{:keys [body]}]
      (let [{:keys [token user]} body]
           (cookies/set! :brevity-token token)
           (accountant/navigate! (routes/page :index))
           (xhr/send-get (routes/api :get-account-info)
                         :success-atom s/session)))

(defn login [email password message]
      (reset! message "Logging in...")
      (xhr/send-post
        (routes/api :login)
        :data {:email email :password password}
        :on-success successful-login
        :on-error #(reset! message "Invalid email or password.")))

(defn logout []
      (xhr/send-delete
        (routes/api :logout)
        :data @s/session
        :on-success
        (fn []
            (cookies/set! :brevity-token "")
            (reset! s/session nil))))
