(ns blogscot.snake)

(defn move! [snake]
  (let [{:keys [x y dx dy]} @snake]
    (swap! snake assoc :x (+ x dx) :y (+ y dy))))

(defn draw [ctx {:keys [x y]}]
  (set! (.-fillStyle ctx) "#fff")
  (.fillRect ctx x y 10 10)
  (.stroke ctx))

(comment

  (def snake (atom {:x 0 :y 0 :dx 0 :dy 1}))
  @snake
  (let [{:keys [x y dx dy]} @snake]
    (swap! snake assoc :x (+ x dx) :y (+ y dy)))
  nil)