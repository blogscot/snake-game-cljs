(ns blogscot.app
  (:require [blogscot.snake :refer [draw-snake draw-apple direction generate-apple move! snake game-dimensions]]))

(def canvas (js/document.querySelector "canvas"))
(def ctx (.getContext canvas "2d"))
(def refresh-rate 200) ;; milliseconds

(def bounded-move! (move! game-dimensions))

(defn draw-canvas [{:keys [width height size]}]
  (doto canvas
    (#(set! (.-width %) (* width size)))
    (#(set! (.-height %) (* height size)))
    (#(set! (.-style %) "background-color: #333"))))

(defn clear-canvas []
  (let [{:keys [width height size]} game-dimensions]
    (.clearRect ctx 0 0 (* width size) (* height size))))

(defn game-loop []
  (bounded-move! snake)
  (clear-canvas)
  (draw-apple ctx)

  (draw-snake ctx @snake))

(defn handle-keypress [e]
  (case (-> e .-key)
    "ArrowUp"    (direction {:dx 0  :dy -1})
    "ArrowDown"  (direction {:dx 0  :dy 1})
    "ArrowLeft"  (direction {:dx -1 :dy 0})
    "ArrowRight" (direction {:dx 1  :dy 0})
    nil))

(defn game []
  (draw-canvas game-dimensions)

  (js/document.addEventListener
   "keyup"
   handle-keypress)

  (generate-apple game-dimensions)
  (game-loop)
  (js/setInterval game-loop refresh-rate))

(defn ^:export init []
  (game))
