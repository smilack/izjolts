(ns izjolts.utils
  (:require [play-clj.core :refer [bundle]]
            [play-clj.g2d :refer [texture texture! texture*]]))

(def piece-names [:I :Z :J :O :L :T :S])
(def texture-names (conj piece-names :border))

(def ^:const side 30)

(def ^:const bucket-width 10)
(def ^:const bucket-height 20)

(def border-width (+ bucket-width 2))
(def border-height (+ bucket-height 2))

(def screen-width (* side border-width 2))
(def screen-height (* side border-height))

(def ^:const start-x 3)
(def ^:const start-y 16)

(def ^:const on-deck-x 15)
(def ^:const on-deck-y 15)

(defn block-textures
  [filename]
  (let [sheet (texture filename)
        matrix (texture! sheet :split side side)
        row (first matrix)
        textures (map texture* row)
        blocks (zipmap texture-names textures)]
    blocks))

(defn block-border
  [block scr-x scr-y w h]
  (let [x (* side scr-x)
        y (* side scr-y)
        height (range h)
        width (range 1 (dec w))
        left (map #(assoc block :x x :y (+ y (* side %))) height)
        right (map #(assoc block :x (+ x (* side (dec w))) :y (+ y (* side %))) height)
        bottom (map #(assoc block :x (+ x (* side %)) :y y) width)
        top (map #(assoc block :x (+ x (* side %)) :y (+ y (* side (dec h)))) width)
        border-pieces (concat left right bottom top)
        border (apply bundle border-pieces)]
    border))


(defn bucket->screen
  "Converts coordinates in the bucket to coordinates on the screen, with (0, 0)
   at the bottom left."
  [bucket-x bucket-y]
  [(* side (inc bucket-x)) (* side (inc bucket-y))])

(defn find-e
  [label entities]
  (some #(if (label %) %) entities))