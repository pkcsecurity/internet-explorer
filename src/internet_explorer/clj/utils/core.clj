(ns internet-explorer.clj.utils.core
  (:require [clojure.string :as str]
            [camel-snake-kebab.core :as csk]
            [buddy.hashers :as hash]))

; https://funcool.github.io/buddy-hashers/latest/#algorithm-tunning-params
(def pbkdf-alg {:alg :scrypt
                :cpucost 65536
                :memcost 10
                :parallelism 1})

(defn pbkdf [pw]
  (hash/derive pw pbkdf-alg))

(defn pbkdf= [pw verifier]
  (hash/check pw verifier {:limit #{(:alg pbkdf-alg)}}))

(def blank? str/blank?)

(def not-blank? (complement blank?))

; https://closure-library.googlecode.com/git-history/docs/local_closure_goog_format_emailaddress.js.source.html#line142
(defn valid-email? [s]
  (re-matches #"^[+a-zA-Z0-9_.!#$%&'*\/=?^`{|}~-]+@([a-zA-Z0-9-]+\.)*[a-zA-Z0-9]{2,63}$" s))

(defn parse-long [x]
      (try (Long/parseLong x)
           (catch Exception _ nil)))
