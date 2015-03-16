(ns compare.handler
  (:use compare.methods)
  (:require [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :as middleware]
            [selmer.parser :refer [render-file]]
            [prone.middleware :refer [wrap-exceptions]]
            [environ.core :refer [env]]))

(defroutes routes
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))

  (GET "/world-cup" [] (render-file "templates/world-cup.html" {:dev (env :dev?)}))


  (GET "/brett"
       []
       "This is from brett")

  (GET "/load/:id"
              [id]
              (load-record id))

  (GET "/load"
              []
              (load-all))

  (GET "/test"
       []
       {:body {:name "test", :city "austin", :state "tx"}})

  (resources "/")
  (not-found "Not Found"))

(def app
  (-> routes
      middleware/wrap-json-body
      middleware/wrap-json-response
      (wrap-defaults api-defaults)))
      ;;((if (env :dev?) wrap-exceptions))))
