(ns izjolts.pieces
  (:require [izjolts.utils :as u]
            [izjolts.data :as d]
            [play-clj.core :refer [bundle]]))

(defn not-monomino-conflict?
  [entities bucket-x bucket-y]
  (every?
    (fn [mono]
      (or
        (not= bucket-x (:bucket-x mono))
        (not= bucket-y (:bucket-y mono))))
    (filterv :monomino? entities)))

(defn can-move?
  [entities piece dx dy]
  (every?
    (fn [piece-mono]
      (let [new-x (+ (:bucket-x piece-mono) dx)
            new-y (+ (:bucket-y piece-mono) dy)]
        (and
          (<= 0 new-x)
          (< new-x u/bucket-width)
          (<= 0 new-y)
          (< new-y u/bucket-height)
          (not-monomino-conflict? entities new-x new-y))))
    (:entities piece)))

(defn move-monomino
  [monomino dx dy]
  (let [bucket-x (+ (:bucket-x monomino 0) dx)
        bucket-y (+ (:bucket-y monomino 0) dy)
        [x y] (u/bucket->screen bucket-x bucket-y)]
    (assoc monomino :x x :y y :bucket-x bucket-x :bucket-y bucket-y :monomino? true)))

(defn move-piece
  [piece dx dy]
  (let [new-entities (map #(move-monomino % dx dy) (:entities piece))
        new-x (+ dx (:bucket-x piece 0))
        new-y (+ dy (:bucket-y piece 0))]
    (assoc piece :entities new-entities :bucket-x new-x :bucket-y new-y)))

(defn shift-piece
  [entities piece direction]
  (let [dx (case direction
             :down 0
             :left -1
             :right 1)
        dy (case direction
             :down -1
             :left 0
             :right 0)]
    (if (can-move? entities piece dx dy)
      (move-piece piece dx dy)
      nil)))

(defn call-up
  [on-deck]
  (-> on-deck
    (dissoc :on-deck?)
    (assoc :current? true)
    (move-piece (- u/start-x u/on-deck-x) (- u/start-y u/on-deck-y))))

(defn piece
  [name blocks {:keys [rotation x y]}]
  (let [block (name blocks)
        matrix (-> d/pieces name (get rotation) reverse) ; flip vertically to match coordinate system
        rows (count matrix)
        cols (count (first matrix))
        monominoes (for [r (range rows) c (range cols) :when (-> matrix (nth r) (nth c))]
                     (move-monomino block c r))
        piece-bundle (apply bundle monominoes)
        placed-piece (move-piece piece-bundle x y)]
    (assoc placed-piece :name name :rotation 0 :bucket-x x :bucket-y y)))

(defn random-piece
  [blocks]
  (let [name (rand-nth u/piece-names)]
    (assoc (piece name blocks {:rotation 0 :x u/on-deck-x :y u/on-deck-y}) :on-deck? true)))

(defn rotate-piece
  [blocks entities current]
  (let [name (:name current)
        rotation (:rotation current)
        num-rotations (-> d/pieces name count)
        next-rotation (mod (inc rotation) num-rotations)
        x (:bucket-x current)
        y (:bucket-y current)
        rotated (piece name blocks {:rotation next-rotation :x x :y y})]
    (if (can-move? entities rotated 0 0)
      (assoc current :entities (:entities rotated) :rotation next-rotation)
      nil)))