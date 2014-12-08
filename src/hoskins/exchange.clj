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
  ([ex name topic]
     (subscribe-ex ex name topic (chan)))
  ([ex name topic c]
     (let [c-info (get ex name)
           loc (if-let [loc (:loc c-info)]
                 loc
                 (async/pub (:chan c-info) :topic))]
       (async/sub loc topic c)
       [ex c])))

(defprotocol IExchange
  (lookup [ex name spec])
  (subscribe [ex name topic]))

(defrecord LocalExchange [ex-atom]
  IExchange
  (lookup [_ name spec]
    ;; Todo read spec
    (if-let [c (get-ex @ex-atom name)]
      c
      (let [c (chan)]
        (swap! ex-atom set-ex name c)
        c)))
  (subscribe [_ name topic]
    (let [c (chan)]
      (let [sub-fn (fn [ex name topic c]
                     (first (subscribe-ex ex name topic c)))]
        (swap! ex-atom sub-fn name topic c))
      c)))

(defn local-exchange []
  (->LocalExchange (atom empty-exchange)))
