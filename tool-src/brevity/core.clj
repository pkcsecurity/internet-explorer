(ns brevity.core
  (:require [clojure.java.shell :refer [sh]]
            [internet-explorer.clj.models.sql :as sql]
            [migratus.core :as migratus]
            [stencil.core :as stencil]))

(defn print-error [err]
      (println (str "Error running command: " err)))

(defn execute-command [shell-command]
      (println (str ">>>RUNNING: " shell-command))
      (let [{:keys [exit out err]} (apply sh (clojure.string/split shell-command #" "))]
           (if (= 1 exit)
             (print-error err)
             (do
               (print out)
               (println ">>>Success!")))))

(def default-command #(execute-command "echo Haven't implemented this command yet..."))

(defn generate-scaffolding [name [entity]]
      (let [data {:name name
                  :entity entity
                  :entity-plural (str entity "s")}
            result (stencil/render-string (slurp "tool-src/templates/entity.clj") data)]
           (spit (str "src/" (:name data) "/clj/models/" (:entity data) ".clj") result)))

(defn generate [name [c & commands]]
      (case c
            "scaffolding" (generate-scaffolding name commands)
            (println "Not a valid command!")))

(def migratus-spec
  {:store :database
   ; The migration dir is relative to /resources, so .sql files will be dropped in resources/private/migrations.
   :migration-dir "private/migrations"
   :db sql/dbspec})

(defn migrate-new [[migration-name]]
      (println "Creating up.sql and down.sql for" migration-name "in" (:migration-dir migratus-spec))
      (migratus/create migratus-spec migration-name))

(defn migration-id [[id-command]]
      (Long/parseLong id-command))

(defn wait-for-db! []
      (try
        (when-let [pending-embedded-database (sql/init!)]
                  @pending-embedded-database)
        (catch Exception _
          (println "Could not spin up a development database, so we'll attempt to connect to an"
                   "already-running instance."))))

(defn migrate [name [c & commands]]
      (let [embedded-postgres (wait-for-db!)]
           (case c
                 ; If you want to run migrations against an application as it is running in development
                 ; mode, you can run "DEV_DATABASE=false lein brevity migrate" and the migration engine
                 ; will connect to the embedded database without trying to spin up its own.
                 "new" (migrate-new commands)
                 "up" (migratus/up migratus-spec (migration-id commands))
                 "down" (migratus/down migratus-spec (migration-id commands))
                 "undo" (migratus/rollback migratus-spec)
                 nil (migratus/migrate migratus-spec)
                 (println "Not a valid migrate command."))
           (when embedded-postgres
                 (.close embedded-postgres))))

(defn handle-commands [c & [command & commands]]
      (let [name (first (clojure.string/split c #"\."))]
           (case command
                 "generate" (generate name commands)
                 "migrate" (migrate name commands)
                 "start" (default-command)
                 (println "Not a valid command!"))
           (shutdown-agents)))
