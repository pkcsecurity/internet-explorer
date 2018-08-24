(ns {{name}}.models.{{entity}}
  (:refer-clojure :exclude [update])
  (:require [{{name}}.models.sql :as sql]
            [{{name}}.utils.core :as u]
            [clojure.spec.alpha :as s]
            [{{name}}.utils.spec :as spec]))

(def table (keyword "{{name}}.{{entity-plural}}"))

(s/def ::uuid spec/uuid)
(s/def ::name spec/string)
(s/def ::facebookid (s/or :using-facebook spec/numeric
                      :has-password nil?))
(s/def ::email spec/email)
(s/def ::password (s/or :has-password spec/strong-password
                    :using-facebook nil?))

(s/def ::phone spec/string)

(def user-spec
  (s/keys :req-un [::uuid
                   ::name
                   ::facebookid
                   ::email
                   ::password
                   ::phone]))

(defn insert [name facebook-id email password]
  (sql/insert! table {:name name
                      :facebookid facebook-id
                      :email email
                      :password (u/pbkdf password)}))

(s/fdef insert
  :args (s/cat :name ::name
          :facebook-id ::facebookid
          :email ::email
          :password ::password))

(defn uuid->user [uuid]
  (first (sql/query ["SELECT * FROM {{name}}.{{entity-plural}} WHERE uuid = ?" uuid])))

(s/fdef uuid->user
  :args (s/cat :uuid ::uuid)
  :ret user-spec)

(defn update [id {:keys [name email password phone] :as user-map}]
  (sql/update! table user-map ["id = ?" id]))

(s/fdef update
  :args (s/cat :id ::uuid
          :user-map (s/keys :opt-un [::name
                                     ::email
                                     ::password
                                     ::phone]))
  :ret user-spec)

(defn delete [email]
  (sql/delete! table ["email = ?" email]))

(s/fdef delete
  :args (s/cat :email ::email))
