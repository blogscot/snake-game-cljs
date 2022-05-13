(ns blogscot.app
  (:require [blogscot.snake :refer [draw-snake draw-apple direction generate-apple move! snake game-dimensions game-state
                                    reset-game!]]))

(def canvas (js/document.querySelector "canvas"))
(def ctx (.getContext canvas "2d"))

(defn draw-canvas [{:keys [width height size]}]
  (doto canvas
    (#(set! (.-width %) (* width size)))
    (#(set! (.-height %) (* height size)))
    (#(set! (.-style %) "background-color: #333"))))

(defn clear-canvas []
  (let [{:keys [width height size]} game-dimensions]
    (.clearRect ctx 0 0 (* width size) (* height size))))

(defn display-welcome-message []
  (set! (.-textAlign ctx) "center")
  (set! (.-fillStyle ctx) "white")
  (set! (.-font ctx) "40px Advent Pro")
  (.fillText ctx "Snake Game" 300 170)
  (set! (.-font ctx) "20px Advent Pro")
  (.fillText ctx "Press 'S' to start" 300 220))

(defn display-game-over []
  (set! (.-textAlign ctx) "center")
  (set! (.-fillStyle ctx) "white")
  (set! (.-font ctx) "40px Advent Pro")
  (.fillText ctx "Game Over" 300 170)
  (set! (.-font ctx) "20px Advent Pro")
  (.fillText ctx "Press 'R' to restart" 300 220))

(defn game-loop []
  (cond
    (not (:running @game-state)) (display-welcome-message)
    (:game-over @game-state) (display-game-over)
    :else (do (move! snake)
              (clear-canvas)))
  (draw-apple ctx)
  (draw-snake ctx @snake)
  (js/setTimeout game-loop (:refresh-rate @game-state)))

(defn handle-keypress [e]
  (case (-> e .-key)
    "ArrowUp"    (direction {:dx 0  :dy -1})
    "ArrowDown"  (direction {:dx 0  :dy 1})
    "ArrowLeft"  (direction {:dx -1 :dy 0})
    "ArrowRight" (direction {:dx 1  :dy 0})
    ("P" "p")    (swap! game-state update :paused not)
    ("S" "s")    (swap! game-state assoc :running true)
    ("R" "r")    (when (true? (:game-over @game-state))
                   (reset-game!)
                   (generate-apple))
    nil))

(defn game []
  (draw-canvas game-dimensions)

  (js/document.addEventListener
   "keyup"
   handle-keypress)

  (generate-apple)
  (game-loop))

(defn ^:export init []
  (game))
