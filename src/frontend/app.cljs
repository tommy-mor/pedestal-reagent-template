(ns frontend.app
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn todo-app []
  [:h1 "reagent"])

(defn ^:export mount-root []
  (rdom/render [todo-app] (js/document.getElementById "app")))

