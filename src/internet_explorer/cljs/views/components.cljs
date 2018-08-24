(ns internet-explorer.cljs.views.components
  (:require [reagent.core :as r]
            [internet-explorer.cljc.routes :as routes]
            [internet-explorer.cljs.controllers.session :as s]
            [internet-explorer.cljs.models.session :as m]
            [internet-explorer.cljs.xhr :as xhr]))

(defn header-link [href text class & attrs]
      [:div
       {:class (str "fl bg-dark-gray pa3 dim pointer " class)}
       [:a.no-underline.white {:href href} text]])

(defn welcome-message [{:keys [full-name] :as session}]
      [:div.fr.relative.hide-child
       (if session
         [:div
          [:div.pa3
           [:i.fas.fa-chevron-down.mr2]
           full-name]
          [:div.bg-gray.pa3.dim.pointer.child.absolute.w-100.dim.tc
           {:on-click (fn [e] (.preventDefault e) (s/logout))}
           "Logout"]]
         [header-link (routes/page :login) "Login" "fr"])])

(defn header []
      (xhr/send-get (routes/api :get-account-info) :success-atom m/session)
      (fn []
          [:header.white.cf.bg-dark-gray.tracked-mega.small-caps
           [header-link (routes/page :index) "Home" "fl"]
           [header-link (routes/page :blog) "Blog" "fl"]
           [welcome-message @m/session]]))

(defn loading-spinner []
      [:div.tc.mv3.gray
       [:i.fas.fa-spinner.fa-pulse]])

(defn footer []
      [:div.tc.mv2
       "External link to " [:a {:href "https://github.com/pkcsecurity/brevity"} "brevity"]])

(defn button [text on-click]
      [:button.pointer.grow.w-100.bg-light-gray.b--dark-gray.ba.bw2.tc.tracked.pv1
       {:on-click on-click}
       text])

(def input-default-style {})

(def input-default-classes "b--light-gray")

(def input-invalid-classes "b--red")

(def input-focus-classes "b--gray")

(def input-invalid-focus-classes "light-red")

(defn nop [& _] nil)

(defn wrap-label [label-text id component]
      (if label-text
        [:div.mv3.cf
         [:label.fl.db.f6 {:for id} label-text]
         component]
        component))

(defn input [& {:keys [label-text class style value placeholder valid? on-change type
                       autofocus? id on-key-press on-blur on-focus]
                :or {on-change nop
                     on-focus nop
                     on-blur nop
                     placeholder ""
                     on-key-press nop
                     valid? true
                     type :text}}]
      ; TODO explore separating out components as a library
      ; TODO tie this into the cljc validators
      (let [focus? (r/atom false)
            value (or value (r/atom ""))]
           (fn [& {:keys [placeholder valid? on-change type autofocus? id on-key-press on-blur on-focus tag-type]
                   :or {on-change nop
                        on-blur nop
                        on-focus nop
                        placeholder ""
                        on-key-press nop
                        valid? true
                        type :text
                        tag-type :input}}]
               [wrap-label
                label-text id
                [tag-type
                 {:type type
                  :autoFocus autofocus?
                  :id (when id id)
                  :placeholder placeholder
                  :on-focus (fn []
                                (reset! focus? true)
                                (on-focus))
                  :on-blur (fn []
                               (reset! focus? false)
                               (on-blur))
                  :on-key-press (fn [e]
                                    (when (= (.-key e) "Enter")
                                          (on-key-press e)))
                  :on-change (fn [e]
                                 (reset! value
                                         (if (= type :checkbox)
                                           (.. e -target -checked)
                                           (.. e -target -value)))
                                 (on-change value e))
                  :value @value
                  :class (str
                           "input pa2 border p shadow-none outline-none ba bt bl bw1 "
                           (cond
                             (and (not valid?) @focus?) input-invalid-focus-classes
                             (not valid?) input-invalid-classes
                             @focus? input-focus-classes
                             :else input-default-classes)
                           (if (= type :checkbox)
                             " dib wa fr "
                             " w-100 ")
                           class)
                  :style (merge input-default-style style)}]])))
