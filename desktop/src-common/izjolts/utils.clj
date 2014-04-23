(ns izjolts.utils
  (:require [izjolts.pieces :as p]
            [play-clj.core :refer [bundle]]
            [play-clj.g2d :refer [texture texture! texture*]]))

(def ^:const side 30)

(def ^:const bucket-width 10)
(def ^:const bucket-height 20)

(def border-width (+ bucket-width 2))
(def border-height (+ bucket-height 2))

(def screen-width (* side border-width 2))
(def screen-height (* side border-height))

(def texture-names (conj p/piece-names :border))

(defn block-textures
  [filename]
  (let [sheet (texture filename)
        matrix (texture! sheet :split side side)
        row (first matrix)
        textures (map texture* row)
        blocks (zipmap texture-names textures)]
    blocks))

(defn bucket-border
  [block]
  (let [height (range border-height)
        width (range 1 (dec border-width))
        left (map #(assoc block :x 0 :y (* side %)) height)
        right (map #(assoc block :x (* side (dec border-width)) :y (* side %)) height)
        bottom (map #(assoc block :x (* side %) :y 0) width)
        top (map #(assoc block :x (* side %) :y (* side (dec border-height))) width)
        border-pieces (concat left right bottom top)
        border (apply bundle border-pieces)]
    border))
        
        
    