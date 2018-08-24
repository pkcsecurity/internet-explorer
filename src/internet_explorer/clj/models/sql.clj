(ns internet-explorer.clj.models.sql
  (:require [clojure.java.jdbc :as jdbc]
            [environ.core :as environ]
            [hugsql.core :as hug]
            [hugsql-adapter-case.adapters :as adapters])
  (:import [com.opentable.db.postgres.embedded EmbeddedPostgres]
           [java.io File]))

(def dbspec (environ/env :database-url))

(hug/set-adapter! (adapters/kebab-adapter))

(hug/def-db-fns "internet_explorer/sql/articles.sql")
(hug/def-db-fns "internet_explorer/sql/users.sql")

(defn init! []
  (let [dev-mode? (= "true" (environ/env :dev-database))]
    (when dev-mode?
      (let [db-port (Integer/parseInt (environ/env :dev-database-port))
            db (-> (EmbeddedPostgres/builder)
                   (.setPort db-port)
                   (.setDataDirectory (File. "resources/private/development-db"))
                   (.setCleanDataDirectory false))]
        (future (.start db))))))
