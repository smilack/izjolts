(ns izjolts.core.desktop-launcher
  (:require [izjolts.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. izjolts "izjolts" 800 600)
  (Keyboard/enableRepeatEvents true))
