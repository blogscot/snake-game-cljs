(ns blogscot.snake)

(def snake (atom {:x 0 :y 0 :dx 1 :dy 0}))

(defn log [arg]
  (js/console.log (clj->js arg)))

(defn move! [{:keys [width height]}]
  (fn [snake]
    (let [{:keys [x y dx dy]} @snake]
      (when-not (or
                 (and (neg? dx) (zero? x))
                 (and (neg? dy) (zero? y))
                 (and (pos? dx) (= x (- width 10)))
                 (and (pos? dy) (= y (- height 10))))
        (swap! snake assoc :x (+ x dx) :y (+ y dy))))))

(defn draw [ctx {:keys [x y]}]
  (set! (.-fillStyle ctx) "#fff")
  (.fillRect ctx x y 10 10)
  (.stroke ctx))

(defn direction [{:keys [dx dy]}]
  (swap! snake assoc :dx dx :dy dy))
