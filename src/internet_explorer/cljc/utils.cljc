(ns internet-explorer.cljc.utils
    (:require [bidi.bidi :as bidi]))

(defn router [root-path & route-defs]
      ["" {root-path (apply merge-with merge route-defs)
           true :four-o-four}])

(defn with-request-method [method]
      (fn
        ([pattern name]
          {pattern {method {"" name}}})
        ([name]
          {"" {method {"" name}}})))

(def GET (with-request-method :get))
(def POST (with-request-method :post))
(def PUT (with-request-method :put))
(def PATCH (with-request-method :patch))
(def DELETE (with-request-method :delete))

(defn context [root-path & route-defs]
      {root-path (apply merge-with merge route-defs)})

(defn page [pattern name]
      {pattern name})
