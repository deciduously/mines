(ns mines.state
  (:require [reagent.core :refer [atom]]))

;; RAtom

(defonce app-state (atom {:score 0
                          :grid []}))
;; Utility

(defn update-state!
  "access util for 'thing' in app-state ratom"
  [thing f & args]
  (apply swap! app-state update-in thing f args))

(defn mines
  "Get indexes for mine-laying"
  [n]
  (let [squared (* n n)]
    (->> (fn []
           (.floor js/Math (->> (.random js/Math)
                                (* squared)
                                (mod (dec squared)))))
         (repeatedly)
         (take (* 2 n))
         (distinct)
         (take n))))

(defn get-neighbors
  "Returns a vector of all neighboring cells to idx"
  [idx]
  (filter #(let [i (:idx %)]
             (or
              (= i (inc idx))
              (= i (dec idx))
              (= i (+ (dec 9) idx))
              (= i (+ 9 idx))
              (= i (+ (inc 9) idx))
              (= i (- idx (dec 9)))
              (= i (- idx 9))
              (= i (- idx (inc 9)))))
          (get-in @app-state [:grid])))

(defn get-neighbor-mines
  "Returns the number of adjacent cells to idx which contain a mine"
  [idx]
  (->> (get-neighbors idx)
       (filter #(= 10 (:val %)))
       (count)))

;; Interface

(defn increase-score!
  "Increment score by 1"
  []
  (update-state! [:score] inc))

(defn decrease-score!
  "Decrement score by 1"
  []
  (update-state! [:score] dec))

(defn reset-score!
  "Set score to 0"
  []
  (update-state! [:score] #(identity 0)))

(defn blank-game!
  "Reset game grid to fresh 'grid-size' square, all vals zeroed."
  [grid-size]
  (update-state! [:grid]
                 #(into [] (for [idx (take (.pow js/Math grid-size 2) (iterate inc 0))]
                             {:idx idx :val 0 :status "hidden"}))))

(defn lay-mines!
  "Plant mines"
  [num-mines]
  (let [danger (mines num-mines)]
    (update-state! [:grid]
                   #(into [] (map (fn [a]                        ; map returns a LazySeq, we need a vector
                                    (if (some #{(:idx a)} danger)
                                      (assoc a :val 10)
                                      (identity a)))
                                  %)))))

(defn apply-clues!
  "Replace cell vals with number of adjacent mines"
  []
  (update-state! [:grid]
                 #(into []
                        (map (fn [a]
                               (if (= 10 (:val a))
                                 (identity a)
                                 (assoc a :val (get-neighbor-mines (:idx a)))))
                             %))))

(defn new-game!
  "Resets game"
  [grid-size num-mines]
  (blank-game! grid-size)
  (lay-mines! num-mines)
  (apply-clues!))

(defn reveal-neighbors!
  "Call reveal! on each neighboring cell"
  [idx]
  (doseq [idx (map :idx (get-neighbors idx))] (reveal! idx)))

;TODO clicking a mine means game over
(defn reveal!
  "Uncover cell at idx"
  [idx]
  (let [cell (get-in @app-state [:grid idx])
        status (:status cell)]
    (cond
      (= status "flagged") #()
      (= status "hidden") (do
                            (update-state! [:grid idx] #(assoc % :status "revealed"))
                            (if (= 0 (:val cell)) (reveal-neighbors! idx))))))

(defn toggle-flag!
  "Toggle flag at idx"
  [idx]
  (if (= "hidden" (get-in @app-state [:grid idx :status]))
    (do (increase-score!)
        (update-state! [:grid idx] #(assoc % :status "flagged")))
    (do (decrease-score!)
        (update-state! [:grid idx] #(assoc % :status "hidden")))))
