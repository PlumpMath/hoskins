(ns hoskins.exchange-test
  (:require [hoskins.exchange :refer :all]
            [midje.sweet :refer :all]
            [clojure.core.async :refer [chan put! <!!] :as async]))

(unfinished)

(facts "about an exchange"
       (fact "calling get-ex on an exchange returns nil"
             (get-ex empty-exchange :channel) => nil)
       (fact "setting a channel means you can get it"
             (let [c1 (chan)
                   c2 (chan)]
               (-> empty-exchange (set-ex :channel1 c1) (get-ex :channel1)) => c1
               (-> empty-exchange (set-ex :channel2 c2) (get-ex :channel2)) => c2)))

(facts "about subscribing to a channel"
       (let [c (chan)
             ex (set-ex empty-exchange :channel c)
             [ex sub-c1] (subscribe-ex ex :channel :topic1)
             [ex sub-c2] (subscribe-ex ex  :channel :topic2)]
         (fact "all channels are unique"
               sub-c1 =not=> c
               sub-c2 =not=> c
               sub-c1 =not=> sub-c2)
         (fact "putting to a channel exits to the sub channels"
               (let [msg1 {:topic :topic1}
                     msg2 {:topic :topic2}]
                 (put! c msg1)
                 (put! c msg2)
                 (fact "msg1 arrives on c1"
                       (<!! sub-c1) => msg1)
                 (fact "msg2 arrives on c2"
                       (<!! sub-c2) => msg2)))))

(facts "about a local exchange"
       (let [ex (local-exchange)
             c (lookup ex :channel :chan)]
         (fact "lookup returns the same channel"
               (lookup ex :channel :chan) => c)
         (fact "Can put and get to the channel"
               (let [msg :message]
                 (put! c msg)
                 (<!! c) => msg))
         (fact "can subscribe to the channel"
               (let [msg1 {:topic :topic1 :message "Hello world"}
                     msg2 {:topic :topic2 :message "Hello world 2"}
                     sub-c1 (subscribe ex :channel :topic1)
                     sub-c2 (subscribe ex :channel :topic2)]
                 (put! c msg1)
                 (put! c msg2)
                 (<!! sub-c1) => msg1
                 (<!! sub-c2) => msg2))))
