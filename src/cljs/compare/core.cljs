(ns compare.core
    (:require [reagent.core :as reagent :refer [atom]]
              ;;[reagent.session :as session]
              ;;[secretary.core :as secretary :include-macros true]
              ;;[goog.events :as events]
              ;;[goog.history.EventType :as EventType]
              [cljsjs.react :as react]
              [ajax.core :refer [GET json-response-format]])

    ;;(:import goog.History))
)

;; -------------------------
;; Global State
(defonce teams (atom (vector)))


;; ----------
;; Actions

(defn increment-games-played [team]
  (if (not (nil? team))
    (if (contains? team :games)
      (update-in team [:games] inc))))

(defn increment-all [data]
  (mapv increment-games-played @teams))

(defn add-gp []
  (swap! teams increment-all))

(defn increment-germany [team]
  (if (= (compare (:team team) "Germany") 0)
    (update-in team [:games] inc)
    team))

(defn increment-ger [team]
  (mapv increment-germany @teams))

(defn add-ger []
  (swap! teams increment-ger))

;; -------------------------
;; Views

;;(defn home-page []
;;  [:div [:h2 "Welcome to compare"]
;;   [:div [:a {:href "#/about"} "go to about page"]]
;;   [:div [:a {:href "#/results"} "go to results page"]]])

;;(defn about-page []
;;  [:div [:h2 "About compare"]
;;   [:div [:a {:href "#/"} "go to the home page"]]])

;;(defn current-page []
;;  [:div [(session/get :current-page)]])

(defn grid-row [team]
  [:tr {:key (str "row-" (:team team))}
   [:td {:key (str "row-" (:team team) "-cell-rank") :class (if (= (mod (:rank team) 2) 0) "odd") } (:rank team)]
   [:td {:key (str "row-" (:team team) "-cell-team") :class (if (= (mod (:rank team) 2) 0) "odd") } (:team team)]
   [:td {:key (str "row-" (:team team) "-cell-win") :class (if (> (:win team) 2) "green" "") } (:win team)]
   [:td {:key (str "row-" (:team team) "-cell-loss") :class (if (> (:loss team) 3) "red" "")} (:loss team)]
   [:td {:key (str "row-" (:team team) "-cell-draw") :class (if (> (:draw team) 2) "blue" "")} (:draw  team)]
   [:td {:key (str "row-" (:team team) "-cell-for") :class (if (> (:goals_for team) 10) "green" "")} (:goals_for team)]
   [:td {:key (str "row-" (:team team) "-cell-against") :class (if (> (:goals_against team) 6) "red" "")} (:goals_against team)]
   [:td {:key (str "row-" (:team team) "-cell-diff") :class (if (< (:goal_diff team) 0) "red" "")} (:goal_diff team)]
   [:td {:key (str "row-" (:team team) "-cell-gp") :class (if (> (:games team) 10) "blue" "")} (:games team)]
   [:td {:key (str "row-" (:team team) "-cell-pts") :class (if (> (:points team) 15) "blue" "")} (:points team)]
   [:td {:key (str "row-" (:team team) "-cell-gpg") :class (if (> (:goals_for team) 10) "green" "")} (/ (:goals_for team) (:games team) )]])

(defn games [team]
  (:games team))

(defn games-played [teams]
  (mapv games teams))

(defn total-row []
  [:div {:key "div-total-games"} "Total games played:" (reduce + (games-played @teams))])

(defn result-page []
  [:div [:h2 "Wold Cup 2014 Results"]
   [:div [:a {:href "#/"} "go to the home page"]]
   [:div
    [:div
     [:button {:on-click #(add-gp)} "Add GP"]
     [:button {:on-click #(add-ger)} "Add GER"]]

    [total-row]

    [:table
     [:thead [:th "Rank"] [:th "Nation"] [:th "W"] [:th "L"] [:th "D"] [:th "GF"] [:th "GA"] [:th "GD"] [:th "GP"] [:th "Points"] [:th "GPG" ]]
     [:tbody
      (for [team @teams]
        ^{:key (:team team)} [grid-row team])]]
   ]])


;; -------------------------
;; Routes
;;(secretary/set-config! :prefix "#")

;;(secretary/defroute "/" []
;;  (session/put! :current-page #'home-page))

;;(secretary/defroute "/about" []
;;  (session/put! :current-page #'about-page))

;;(secretary/defroute "/results" []
;;  (session/put! :current-page #'result-page))

;; -------------------------
;; History
;; must be called after routes have been defined
;;(defn hook-browser-navigation! []
;;  (doto (History.)
;;    (events/listen
;;     EventType/NAVIGATE
;;     (fn [event]
;;       (secretary/dispatch! (.-token event))))
;;    (.setEnabled true)))


;; -------------------------
;; load ajax data source
(defn app-state-error [response]
  (.error js/console (:message response)))

(defn app-state-handler [response]
  (swap! teams [] (map-indexed (fn [idx n] (assoc-in n [:rank] (+ idx 1))) (sort-by :points #(compare %2 %1) response)))
  (reagent/render-component [result-page] (.getElementById js/document "app")))

;; -------------------------
;; Initialize app
(defn init! []
  ;;(hook-browser-navigation!)

  (GET "/load" {:handler app-state-handler :error-handler: app-state-error
                :response-format (json-response-format {:keywords? true})}))
