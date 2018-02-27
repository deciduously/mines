(ns mines.core
  (:require [mines.ui :refer [ui]]
            [reagent.core :refer [render-component]]))

(when-let [element (.getElementById js/document "app")]
  (render-component [ui] element))
