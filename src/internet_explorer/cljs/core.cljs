(ns internet-explorer.cljs.core
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.session :as session]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]
            [internet-explorer.cljc.routes :as routes]
            [internet-explorer.cljs.controllers.core :as controllers]
            [internet-explorer.cljs.views.core :as views]))

(enable-console-print!)

(defn on-js-reload []
      (reagent/render-component [views/layout]
                                (. js/document (getElementById "app"))))

(defn -main []
      (accountant/configure-navigation!
        {:nav-handler (fn [path]
                        (let [match (bidi/match-route routes/page-routes path)
                              current-page (:handler match)
                              route-params (:route-params match)
                              initializer (controllers/page-initializers current-page)]
                             (session/put! :route {:current-page current-page
                                                   :route-params route-params})
                             (when initializer (initializer route-params))))
         :path-exists? (fn [path]
                           (boolean (bidi/match-route routes/page-routes path)))})
      (accountant/dispatch-current!)
      (on-js-reload))

(-main)
