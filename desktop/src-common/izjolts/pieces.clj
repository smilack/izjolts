(ns izjolts.pieces
  (:require [izjolts.utils :as u]
            [izjolts.data :as d]
            [play-clj.core :refer [bundle]]))

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
          (every?
            (fn [bucket-mono]
              (or
                (not= new-x (:bucket-x bucket-mono))
                (not= new-y (:bucket-y bucket-mono))))
            (filterv :monomino? entities)))))
    (:entities piece)))

(defn move-monomino
  [monomino dx dy]
  (let [bucket-x (+ (:bucket-x monomino 0) dx)
        bucket-y (+ (:bucket-y monomino 0) dy)
        [x y] (u/bucket->screen bucket-x bucket-y)]
    (assoc monomino :x x :y y :bucket-x bucket-x :bucket-y bucket-y :monomino? true)))

(defn move-piece
  [piece dx dy]
  (let [new-entities (map #(move-monomino % dx dy) (:entities piece))]
    (assoc piece :entities new-entities)))

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
  "Creates a new piece in the on-deck area with default rotation."
  [name blocks]
  (let [block (name blocks)
        matrix (-> d/pieces name first reverse) ; flip vertically to match coordinate system
        rows (count matrix)
        cols (count (first matrix))
        monominoes (for [r (range rows) c (range cols) :when (-> matrix (nth r) (nth c))]
                     (move-monomino block c r))
        piece-bundle (apply bundle monominoes)]
    (move-piece piece-bundle u/on-deck-x u/on-deck-y)))

(defn random-piece
  [blocks]
  (let [name (rand-nth u/piece-names)]
    (assoc (piece name blocks) :on-deck? true)))