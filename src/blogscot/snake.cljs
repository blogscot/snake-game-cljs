(ns blogscot.snake)

(def game-dimensions {:width 60 :height 40 :size 10})

(def snake (atom {:x 0 :y 0 :dx 1 :dy 0}))
(def apple (atom {:x 0 :y 0 :color "#fff" :visible true}))

(defn log [& args]
  (js/console.log (clj->js args)))

(defn point->rect
  "Converts a [x y] point into a screen rectangle 
   [screen-x screen-y width height]"
  [x y]
  (let [block-size (:size game-dimensions)]
    [(* x block-size) (* y block-size) block-size block-size]))

(defn apple-eaten? []
  (= ((juxt :x :y) @snake) ((juxt :x :y) @apple)))

(defn generate-apple [{:keys [width height]}]
  (let [x (rand-int (dec width))
        y (rand-int (dec height))
        color (rand-nth ["red" "green"])]
    (swap! apple assoc :x x :y y :color color :visible true)))

(defn update-positions [x y]
  (swap! snake assoc :x x :y y)
  (when (apple-eaten?)
    (swap! apple assoc :visible false)
    (js/setTimeout #(generate-apple game-dimensions) (rand-int 3000))))

(defn move! [{:keys [width height]}]
  (fn [snake]
    (let [{:keys [x y dx dy]} @snake]
      (when-not (or
                 (and (neg? dx) (zero? x))
                 (and (neg? dy) (zero? y))
                 (and (pos? dx) (= x (dec width)))
                 (and (pos? dy) (= y (dec height))))
        (update-positions (+ x dx) (+ y dy))))))

(defn draw [ctx x y color]
  (let [[rx ry w h] (point->rect x y)]
    (set! (.-fillStyle ctx) color)
    (.fillRect ctx rx ry w h)
    (.stroke ctx)))

(defn draw-snake [ctx {:keys [x y]}]
  (draw ctx x y "#fff"))

(defn draw-apple [ctx]
  (let [{:keys [x y color visible]} @apple]
    (when (true? visible)
      (draw ctx x y color))))

(defn direction [{:keys [dx dy]}]
  (swap! snake assoc :dx dx :dy dy))
