(ns izjolts.pieces
  (:require [izjolts.utils :as u]))

(def piece-names [:I :Z :J :O :L :T :S])

(defn piece
  "Creates a new piece in the starting area with default rotation."
  [name]
  nil)

(defn move-piece
  [piece direction]
  (let [dx (case direction
             :down 0
             :left -1
             :right 1)
        dy (case direction
             :down -1
             :left 0
             :right 0)]
    (if (can-move? piece dx dy)
      (let [bucket-x (+ (:bucket-x piece dx))
            bucket-y (+ (:bucket-y piece) dy)
            [x y] (u/bucket->screen bucket-x bucket-y)]
        (assoc piece :x x :y y :bucket-x bucket-x :bucket-y bucket-y)))))

(defn can-move
  [piece dx dy]
  true)
  