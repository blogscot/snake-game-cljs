(ns blogscot.app
  (:require [blogscot.snake :refer [move! draw]]))

(def canvas (js/document.querySelector "canvas"))
(def ctx (.getContext canvas "2d"))
(def width 600)
(def height 400)

(def snake (atom {:x 0 :y 0 :dx 1 :dy 0}))

(defn log [arg]
  (js/console.log (clj->js arg)))

(defn draw-canvas [width height]
  (doto canvas
    (#(set! (.-width %) width))
    (#(set! (.-height %) height))
    (#(set! (.-style %) "background-color: #333"))))

(defn game-loop []
  (move! snake)
  (.clearRect ctx 0 0 width height)
  (draw ctx @snake))

(defn handle-keypress [e]
  (case (-> e .-key)
    "ArrowUp"    (swap! snake assoc :dx 0  :dy -1)
    "ArrowDown"  (swap! snake assoc :dx 0  :dy 1)
    "ArrowLeft"  (swap! snake assoc :dx -1 :dy 0)
    "ArrowRight" (swap! snake assoc :dx 1  :dy 0)))

(defn game []
  (draw-canvas width height)

  (js/document.addEventListener
   "keyup"
   handle-keypress)

  (game-loop)
  (js/setInterval (fn [] (game-loop)) 100))

(defn ^:export init []
  (game))

(comment

  (def canvas (js/document.querySelector "canvas"))
  (def ctx (.getContext canvas "2d"))
  (js/console.log canvas)

  nil)