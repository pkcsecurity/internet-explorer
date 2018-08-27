(ns internet-explorer.clj.routes.slack
  (:require [ring.util.codec :as codec]
            [internet-explorer.clj.okhttp :as okhttp]
            [environ.core :as environ]
            [cheshire.core :as chesh]))

(def game-in-progress? (atom false))
(def votes (atom {}))
(def scores (atom {}))
(def users (atom {}))

(defn vote [voter choice]
  (swap! votes assoc voter choice))

(defn id->user [user-id]
  (let [url (str "https://slack.com/api/users.info?"
              "token=" (environ/env :slack-bot-token)
              "&user=" user-id)
        {{:keys [real_name id]} :user} (okhttp/sync-req "GET" url)]
    {:name real_name
     :id id}))


(defn action [req]
  (println "Here")
  (if (:challenge (:body req))
    {:status 200
     :body (:challenge (:body req))}
    (do
      (let [payload (chesh/parse-string (get (:params req) "payload") true)
            user (:id (:user payload))
            t (:type payload)
            choice (:value (first (:selected_options (first (:actions payload)))))]
        (println user choice)
        (when user
          (vote user choice)))
      (println @votes)
      (if (>= (count @votes) 2)
        (let [[[winner-id _]] (sort (frequencies (vals @votes)))]
          (when-not (get @users winner-id)
            (swap! users assoc winner-id (:name (id->user winner-id))))
          (swap! scores update winner-id (fnil inc 0))
          (println @scores)
          (println @users)
          (reset! votes {})
          (reset! game-in-progress? false)
          {:status 200
           :body {:text (str (get @users winner-id) " has won the game!")
                  :response_type "in_channel"
                  :attachments [{:text (str "How many dragons have you faced?:\n")
                                 :fallback "You must vote! Choose a hero to face this dragon?"
                                 :color "#00ff00"
                                 :attachment_type "#3AA3E3"
                                 :callback_id "callback_id"}]}})
        {:status 200
         :body nil}))))

(defn start [req]
  #_(println "req")
  #_(println req)
  (when-not @game-in-progress?
    (okhttp/sync-req "POST"
                     "https://hooks.slack.com/services/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"
                     :body {:response_type "in_channel"
                            :attachments [{:text "Lookout! Your team has encountered a Dragon.\nYou must vote! Choose a hero to face this dragon?"
                                           :fallback "You must vote! Choose a hero to face this dragon?"
                                           :color "#ff0000"
                                           :attachment_type "#3AA3E3"
                                           :callback_id "callback_id"
                                           :actions [{:name "winners_list"
                                                      :text "Who will be the hero?"
                                                      :type "select"
                                                      :data_source "users"}]}]}
                     :headers {"Content-Type" "application/json"}
                     :response-type :text)
    (reset! game-in-progress? true))
  {:body "round start"
   :status 200})

