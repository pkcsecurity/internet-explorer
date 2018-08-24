(ns internet-explorer.clj.routes.middleware
  (:require [bidi.bidi :as bidi]))

(defn wrap-bidi [handler wrapped-routes renderer]
      (fn [{:keys [uri path-info] :as req}]
          (let [path (or path-info uri)
                resolved-route (bidi/match-route* wrapped-routes path req)
                resolved-handler (:handler resolved-route)
                route-params (:route-params resolved-route)]
               (if (= :four-o-four resolved-handler)
                 (handler req)
                 (renderer
                   (-> req
                       (update-in [:params] merge route-params)
                       (update-in [:route-params] merge route-params))
                   resolved-handler)))))
