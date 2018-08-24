(ns internet-explorer.cljc.validators)

(defn length-between [min max field-name]
      (fn [input]
          (when-not (< min (count input) max)
                    (str "Please inter a " field-name " between " min " and " max " characters."))))

(defn required [field-name]
      (fn [input]
          (when (empty? input)
                (str "Please enter a valid " field-name "."))))

(def validators
  {:login {:email (required "email address")
           ; When we add a registration form we'll probably want to do it with the cljsjs/zxcvbn package
           :password (required "password")}
   :new-blog-entry {:title (length-between 10 50 "title")
                    :content (length-between 10 10000 "blog post")}})
