(ns izjolts.pieces
  (:require [izjolts.utils :as u]
            [izjolts.data :as d]
            [play-clj.core :refer [bundle]]))

(defn can-move?
  [piece dx dy]
  (every?
    #(and
       (< 0 (+ (:bucket-x %) dx) u/bucket-width)
       (< 0 (+ (:bucket-y %) dy) u/bucket-height)
       (comment "check for existing monominoes"))
    (:entities piece)))

(defn move-monomino
  [monomino dx dy]
  (let [bucket-x (+ (:bucket-x monomino 0) dx)
        bucket-y (+ (:bucket-y monomino 0) dy)
        [x y] (u/bucket->screen bucket-x bucket-y)]
    (assoc monomino :x x :y y :bucket-x bucket-x :bucket-y bucket-y)))

(defn move-piece
  [piece dx dy]
  (let [new-entities (map #(move-monomino % dx dy) (:entities piece))]
    (assoc piece :entities new-entities)))

(defn shift-piece
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
      (move-piece piece dx dy))))

(defn piece
  "Creates a new piece in the starting area with default rotation."
  [name blocks]
  (let [block (name blocks)
        matrix (-> d/pieces name first reverse) ; flip vertically to match coordinate system
        rows (count matrix)
        cols (count (first matrix))
        monominoes (for [r (range rows) c (range cols) :when (-> matrix (nth r) (nth c))]
                     (move-monomino block c r))
        piece-bundle (apply bundle monominoes)]
    (move-piece piece-bundle u/start-x u/start-y)))

(defn random-piece
  [blocks]
  (let [name (rand-nth u/piece-names)]
    (assoc (piece name blocks) :current? true)))