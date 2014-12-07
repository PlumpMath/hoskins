(ns hoskins.exchange-test
  (:require [hoskins.exchange :refer :all]
            [midje.sweet :refer :all]
            [clojure.core.async :refer [chan]]))

(unfinished)

(facts "about an exchange"
       (fact "calling get-ex on an exchange returns nil"
             (get-ex empty-exchange :channel) => nil)
       (fact "setting a channel means you can get it"
             (let [c (chan)]
               (-> empty-exchange (set-ex :channel c) (get-ex :channel)) => c)))
