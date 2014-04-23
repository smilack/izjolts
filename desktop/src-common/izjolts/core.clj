(ns izjolts.core
  (:require [izjolts.pieces :as p]
            [izjolts.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defscreen main-screen
  :on-show
  (fn on-show[screen entities]
    (update! screen :renderer (stage))
    (let [blocks (u/block-textures "blocks.png")]
      (update! screen :blocks blocks)
      (u/bucket-border (:border blocks))))
  
  :on-render
  (fn on-render[screen entities]
    (clear!)
    (render! screen entities)))

(defgame izjolts
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
