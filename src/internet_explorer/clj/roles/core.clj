(ns internet-explorer.clj.roles.core
  (:require [buddy.auth :as auth]
            [buddy.auth.accessrules :as authz]
            [buddy.auth.backends :as backends]
            [buddy.auth.middleware :as mw]
            [internet-explorer.clj.models.sql :as sql]
            [internet-explorer.clj.utils.core :as utils]
            [environ.core :as environ]
            [internet-explorer.clj.utils.core :as u]))

(def session-timeout (or (utils/parse-long (environ/env :session-timeout-minutes)) 150000))
(def idle-timeout (or (utils/parse-long (environ/env :idle-session-timeout-minutes)) 15))

(def allow-all (constantly true))
(defn allow-admin [{:keys [identity]}]
      (:is-admin identity))
(def deny-all (constantly false))

(def rules
  [{:uris ["/admin/*"]
    :handler allow-admin}
   {:uris ["/api/v1/admin/*"]
    :handler allow-admin}
   {:uris ["/*"]
    :handler allow-all}
   {:pattern #"^/.*$"
    :handler deny-all}])

(defn token-auth [request token]
      ; TODO it's probably best to store the tokens hmac'd to guard against timing attacks
      (when-let [session (sql/session-by-id sql/dbspec {:id token})]
                (let [{:keys [since-started since-active]} session
                      expired? (>= since-started session-timeout)
                      inactive? (>= since-active idle-timeout)]
                     (when-not (or expired? inactive?)
                               (sql/keep-session-active sql/dbspec {:id token})
                               session))))

(defn wrap-security [app]
  (let [auth-backend (backends/token {:authfn token-auth})]
    (-> app
        (authz/wrap-access-rules {:rules rules})
        (mw/wrap-authentication auth-backend)
        (mw/wrap-authorization auth-backend))))

