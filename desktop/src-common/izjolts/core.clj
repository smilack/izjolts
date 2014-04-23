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
    
(defn move-current
  [entities direction]
  (let [current (some #(if (:current? %) %) entities)
        moved (p/shift-piece current direction)]
    (if moved
      (replace {current moved} entities))))

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
    (let [direction (condp = keycode
                      (key-code :dpad-down) :down
                      (key-code :dpad-left) :left
                      (key-code :dpad-right) :right
                      nil)]
      (if direction
        (move-current entities direction))))
  
  :on-timer
  (fn on-timer[{:keys [id blocks]} entities]
    (if-not (some #(:current? %) entities)
      (next-piece entities blocks)
      (move-current entities :down))))

(defgame izjolts
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
