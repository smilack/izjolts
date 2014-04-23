(ns izjolts.core
  (:require [izjolts.pieces :as p]
            [izjolts.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn next-piece
  [entities blocks]
  (let [on-deck (some #(if (:on-deck? %) %) entities)
        new-current (p/call-up on-deck)
        new-on-deck (p/random-piece blocks)]
    (conj (replace {on-deck new-current} entities) new-on-deck)))
    

(defscreen main-screen
  :on-show
  (fn on-show[screen entities]
    (update! screen :renderer (stage))
    (let [blocks (u/block-textures "blocks.png")]
      (update! screen :blocks blocks)
      (add-timer! screen :step 1 1)
      (conj entities
            (u/bucket-border (:border blocks))
            (p/random-piece blocks))))
  
  :on-render
  (fn on-render[screen entities]
    (clear!)
    (render! screen entities))
  
  :on-key-down
  (fn on-key-down[{:keys [keycode]} entities]
    entities)
  
  :on-timer
  (fn on-timer[{:keys [id blocks]} entities]
    (if-not (some #(:current? %) entities)
      (next-piece entities blocks)
      (comment "else"))))

(defgame izjolts
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
