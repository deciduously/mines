(ns mines.ui
  (:require [mines.state :as s]))

;; Helpers

(defn http-link
  "href helper"
  ([href]
   [:a {:href href} href])
  ([href text]
   [:a {:href href} text]))

(defn button
  "Button utility, optionally accepts right-click action"
 ([title class action]
  [:input {:type "button" :value title :on-click action :class class}])
 ([title class action right-action]
  [:input {:type "button" :value title :on-click action :onContextMenu right-action :class class}]))

;; Components

(defn debug
  "Debug console"
  []
  [:div
   [:h4 "debug"]
   [button "Reveal All" "user" #(doseq [idx (range 0 81)] (s/reveal! idx))]])

(defn score
  "Score component"
  []
  [:div.score
   (str "Score: " (get-in @s/app-state [:score]))])

(defn cell
  "Cell component"
  [{:keys [status idx val]}]
  (cond
     (= status "hidden") [button
                               " " "cell"
                               #(s/reveal! idx)
                               #(s/toggle-flag! idx)]
     (= status "flagged") [button
                                \u2691 "cell"
                                #() ; can't reveal a flagged cell!
                                #(s/toggle-flag! idx)]
     (= status "revealed") [:div.cell
                            (if (= val 10)
                              [:span.mine "*"]
                              [:span {:class (str "_" val)} (str val)])]))

; switch if to cond, have a separate button with a no-op regular click, and a toggle flag right click

(defn player
  "Player console"
  []
  [:div
   [score]
   [button "New Game!" "user" #(do (s/new-game! 9 10) (s/reset-score!))]]) ; 9x9 grid, 10 mines

(defn grid
  "The minefield"
  [grid]
  [:ul
   (for [c grid]
     ^{:key (:idx c)}
     [cell c])])

;; Main UI

(defn ui
  "Game ui"
  []
  [:div
   [:h1 "MINES!"]
   [:hr] [:br]
   [:div.grid
    [grid (get-in @s/app-state [:grid])]] [:br] [:hr]
   [player]
   [debug] [:br]
   [http-link "http://deciduously.com"] [:br]
   [http-link "https://github.com/deciduously/mines" "github"]])
