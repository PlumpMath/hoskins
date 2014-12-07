(ns hoskins.exchange
  (:require [clojure.core.async :refer [chan] :as async]))

(def empty-exchange {})

(defn get-ex
  [ex name]
  (get-in ex [name :chan]))

(defn set-ex
  [ex name c]
  (assoc-in ex [name :chan] c))

(defn subscribe-ex
  [ex name topic]
  (let [c-info (get ex name)
        loc (if-let [loc (:loc c-info)]
              loc
              (async/pub (:chan c-info) :topic))
        c (chan)]
    (async/sub loc topic c)
    [ex c]))
