(ns blogscot.snake)

(def game-dimensions {:width 60 :height 40 :size 10})

;; Initialisation
(def game-init {:game-over false})
(def snake-init {:body (list [2 0] [1 0] [0 0]) :dx 1 :dy 0})
(def apple-init {:x 0 :y 0 :color "#fff" :visible true})

(def game-state (atom game-init))
(def snake (atom snake-init))
(def apple (atom apple-init))

(defn reset-game! []
  (reset! game-state game-init)
  (reset! snake snake-init)
  (reset! apple apple-init))

(defn log [arg]
  (js/console.log (clj->js arg)))

(defn point->rect
  "Converts a [x y] point into a screen rectangle 
   [screen-x screen-y width height]"
  [x y]
  (let [block-size (:size game-dimensions)]
    [(* x block-size) (* y block-size) block-size block-size]))

(defn apple-eaten? [snake-position]
  (= snake-position ((juxt :x :y) @apple)))

(defn generate-apple []
  (let [{:keys [width height]} game-dimensions
        x (rand-int (dec width))
        y (rand-int (dec height))
        color (rand-nth ["red" "green"])]
    (swap! apple assoc :x x :y y :color color :visible true)))

(defn hit-wall? []
  (let [{:keys [body dx dy]} @snake
        [[x y]] body
        {:keys [width height]} game-dimensions]
    (or
     (and (neg? dx) (zero? x))
     (and (neg? dy) (zero? y))
     (and (pos? dx) (= x (dec width)))
     (and (pos? dy) (= y (dec height))))))

(defn update-positions [[new-x new-y]]
  (if (apple-eaten? [new-x new-y])
    ;; add apple block position as head
    (do (swap! snake update :body conj ((juxt :x :y) @apple))
        (swap! apple assoc :visible false)
        (js/setTimeout #(generate-apple) (rand-int 3000)))
    ;; add new block as head and remove last block
    (let [body (:body @snake)]
      (if (hit-wall?)
        (swap! game-state assoc :game-over true)
        (swap! snake assoc :body (butlast (conj body [new-x new-y])))))))

(defn move! [snake]
  (let [{:keys [body dx dy]} @snake
        [[x y]] body]
    (update-positions [(+ x dx) (+ y dy)])))

(defn draw [ctx x y color]
  (let [[rx ry w h] (point->rect x y)]
    (set! (.-fillStyle ctx) color)
    (.fillRect ctx rx ry w h)
    (.stroke ctx)))

(defn draw-snake [ctx {:keys [body]}]
  (doseq [[x y] body]
    (draw ctx x y "#fff")))

(defn draw-apple [ctx]
  (let [{:keys [x y color visible]} @apple]
    (when (true? visible)
      (draw ctx x y color))))

(defn direction
  "Change direction left, right (even forward) but not reverse"
  [{:keys [dx dy]}]
  (when-not (= ((juxt :dx :dy) @snake) (map #(* -1 %) [dx dy]))
    (swap! snake assoc :dx dx :dy dy)))
