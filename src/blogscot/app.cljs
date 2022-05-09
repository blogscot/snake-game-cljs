(ns blogscot.app
  (:require [blogscot.snake :refer [draw direction move! snake]]))

(def canvas (js/document.querySelector "canvas"))
(def ctx (.getContext canvas "2d"))
(def canvas-dimensions {:width 600 :height 400})

(def bounded-move! (move! canvas-dimensions))

(defn log [arg]
  (js/console.log (clj->js arg)))

(defn draw-canvas [{:keys [width height]}]
  (doto canvas
    (#(set! (.-width %) width))
    (#(set! (.-height %) height))
    (#(set! (.-style %) "background-color: #333"))))

(defn clear-canvas []
  (let [{:keys [width height]} canvas-dimensions]
    (.clearRect ctx 0 0 width height)))

(defn game-loop []
  (bounded-move! snake)
  (clear-canvas)
  (draw ctx @snake))

(defn handle-keypress [e]
  (case (-> e .-key)
    "ArrowUp"    (direction {:dx 0  :dy -1})
    "ArrowDown"  (direction {:dx 0  :dy 1})
    "ArrowLeft"  (direction {:dx -1 :dy 0})
    "ArrowRight" (direction {:dx 1  :dy 0})
    nil))

(defn game []
  (draw-canvas canvas-dimensions)

  (js/document.addEventListener
   "keyup"
   handle-keypress)

  (game-loop)
  (js/setInterval (fn [] (game-loop)) 50))

(defn ^:export init []
  (game))

(comment

  (def canvas (js/document.querySelector "canvas"))
  (def ctx (.getContext canvas "2d"))
  (js/console.log canvas)

  nil)