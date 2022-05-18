(ns blogscot.app
  (:require [blogscot.snake :as s]))

(def canvas (js/document.querySelector "canvas"))
(def ctx (.getContext canvas "2d"))

(defn draw-canvas [{:keys [width height size]}]
  (doto canvas
    (#(set! (.-width %) (* width size)))
    (#(set! (.-height %) (* height size)))
    (#(set! (.-style %) "background-color: #333"))))

(defn clear-canvas []
  (let [{:keys [width height size]} s/game-dimensions]
    (.clearRect ctx 0 0 (* width size) (* height size))))

(defn display-text
  ([coll]
   (doseq [[text x y size] coll]
     (display-text text x y size)))
  ([text x y size]
   (display-text text x y
                 (condp = size
                   "large" "40px Advent Pro"
                   "small" "20px Advent Pro") "white"))
  ([text x y font color]
   (set! (.-textAlign ctx) "center")
   (set! (.-fillStyle ctx) color)
   (set! (.-font ctx) font)
   (.fillText ctx text x y)))

(defn display-welcome-message []
  (clear-canvas)
  (display-text [["Snake Game" 300 160 "large"]
                 ["Use cursor keys to move" 300 200 "small"]
                 ["Press Space to start" 300 240 "small"]]))

(defn display-game-over []
  (let [text (str "Apples Eaten: " (s/get-apples-eaten))
        highscore (s/get-highscore)]
    (display-text [[text 80 20 "small"]
                   [(str "Highscore: " highscore) 530 20 "small"]
                   ["Game Over" 300 170 "large"]
                   ["Press 'R' to replay" 300 220 "small"]])))

(defn display-paused []
  (display-text "Paused" 40 20 "small"))

(defn game-loop []
  (let [{:keys [game-over paused running]} @s/game-state]
    (cond
      (not running)        (display-welcome-message)
      game-over            (display-game-over)
      (and running paused) (display-paused)
      :else (do (s/move! s/snake)
                (clear-canvas)))
    (when running
      (s/draw-apple ctx)
      (s/draw-snake ctx @s/snake)))
  (js/setTimeout game-loop (:refresh-rate @s/game-state)))

(defn handle-keypress [e]
  (case (-> e .-key)
    "ArrowUp"    (s/direction {:dx 0  :dy -1})
    "ArrowDown"  (s/direction {:dx 0  :dy 1})
    "ArrowLeft"  (s/direction {:dx -1 :dy 0})
    "ArrowRight" (s/direction {:dx 1  :dy 0})
    (" ")        (cond
                   (:running @s/game-state)
                   (swap! s/game-state update :paused not)
                   :else (swap! s/game-state assoc :running true))
    ("R" "r")    (when (true? (:game-over @s/game-state))
                   (s/reset-game!)
                   (s/generate-apple))
    nil))

(defn game []
  (draw-canvas s/game-dimensions)

  (js/document.addEventListener
   "keyup"
   handle-keypress)

  (s/generate-apple)
  (game-loop))

(defn ^:export init []
  ;; give external fonts time to load
  (js/setTimeout game 300))
