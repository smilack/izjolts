(ns izjolts.core.desktop-launcher
  (:require [izjolts.core :refer :all]
            [izjolts.utils :as u])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. izjolts "izjolts" u/screen-width u/screen-height)
  (Keyboard/enableRepeatEvents true))
