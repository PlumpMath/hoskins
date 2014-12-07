(ns hoskins.exchange)

(def empty-exchange {})

(defn get-ex
  [ex name]
  (get ex name))

(defn set-ex
  [ex name c]
  (assoc ex name c))
