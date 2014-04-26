(ns izjolts.core
  (:require [izjolts.pieces :as p]
            [izjolts.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.g2d :refer :all]))

(defn next-piece
  [entities blocks]
  (let [on-deck (u/find-e :on-deck? entities)
        new-current (p/call-up on-deck)
        new-on-deck (p/random-piece blocks)]
    (conj (replace {on-deck new-current} entities) new-on-deck)))

(defn rotate-current
  [blocks entities]
  (let [current (u/find-e :current? entities)
        rotated (p/rotate-piece blocks entities current)]
    rotated))

(defn move-current
  [entities direction]
  (let [current (u/find-e :current? entities)
        moved (p/shift-piece entities current direction)]
    moved))

(defn find-complete-lines
  [entities]
  (for [r (range u/bucket-height)
        :when (= u/bucket-width
                 (count (filterv #(= r (:bucket-y % -1)) entities)))]
    r))

(defn filter-lines
  [entities complete-lines]
  (filterv
    (fn [monomino]
      (not-any?
        (fn [line] (= line (:bucket-y monomino)))
        complete-lines))
    entities))

(defn remove-complete-lines
  [entities]
  (let [complete-lines (find-complete-lines entities)
        lines-removed (filter-lines entities complete-lines)
        shift-list (for [n (range 20)] (count (filter (partial > n ) complete-lines)))
        shift-map (zipmap (range 20) shift-list)]
    (map
      (fn [entity]
        (if (and (contains? entity :bucket-y) (:monomino? entity))
          (let [y (:bucket-y entity)
                dy (get shift-map y)]
            (p/move-monomino entity 0 (- dy)))
          entity))
      lines-removed)))

(defscreen main-screen
  :on-show
  (fn on-show[screen entities]
    (update! screen :renderer (stage))
    (let [blocks (u/block-textures "blocks.png")]
      (update! screen :blocks blocks)
      (add-timer! screen :step 1 1)
      (conj entities
            (u/block-border (:border blocks) 0 0 u/border-width u/border-height)
            (u/block-border (:border blocks) u/on-deck-x u/on-deck-y 6 6)
            (p/random-piece blocks))))
  
  :on-render
  (fn on-render[screen entities]
    (clear!)
    (render! screen entities))
  
  :on-key-down
  (fn on-key-down[{:keys [keycode blocks]} entities]
    (if-let [current (u/find-e :current? entities)]
      (if-let [direction (condp = keycode
                           (key-code :dpad-down) :down
                           (key-code :dpad-left) :left
                           (key-code :dpad-right) :right
                           nil)]
        (if-let [moved (move-current entities direction)]
          (replace {current moved} entities))
        (if (= keycode (key-code :dpad-up))
          (if-let [rotated (rotate-current blocks entities)]
            (replace {current rotated} entities))))))
  
  :on-timer
  (fn on-timer[{:keys [id blocks]} entities]
    (if-not (some :current? entities)
      (next-piece entities blocks)
      (if-let [moved (move-current entities :down)]
        (replace {(u/find-e :current? entities) moved} entities)
        (let [current (u/find-e :current? entities)
              monominoes (:entities current)
              e-with-m (concat (filterv (complement :current?) entities) monominoes)]
          (remove-complete-lines e-with-m))))))

(defgame izjolts
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
