(ns double-booked.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [double-booked.core :refer :all]
            [clojure.spec.alpha :as s]
            [clj-time.coerce :as c]
            [clojure.spec.test.alpha :as stest]))

(deftest parse-csv-test
  (testing "parses valid csv"
    (is (= '(["00" "01"]
             ["10" "11"])
           (parse-csv "00,01;10,11")))
    (is (= '(["2019-10-08T15:00:00.000Z" "2019-10-08T16:00:00.000Z"]
             ["2019-10-08T14:00:00.000Z" "2019-10-08T18:00:00.000Z"]
             ["2019-10-08T11:00:00.000Z" "2019-10-08T12:00:00.000Z"]
             ["2019-10-08T11:30:00.000Z" "2019-10-08T15:10:00.000Z"])
           (parse-csv (str "2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;"
                           "2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z;"
                           "2019-10-08T11:00:00.000Z,2019-10-08T12:00:00.000Z;"
                           "2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z")))))
  (testing "trims csv"
    (is (= '(["00" "01"]
             ["10" "11"])
           (parse-csv " 00,    01;10, 11  ")))))


(s/def ::date #(instance? org.joda.time.DateTime (c/from-string %)))
(s/def ::event (s/cat :start ::date :end ::date))
(s/def ::pair (s/coll-of ::event))
(s/fdef overlapping?
        :args (s/coll-of ::pair)
        :ret boolean?)
(stest/instrument `overlapping?)

(deftest overlapping?-test
  (testing "with overlapping events"
    (is (= true (overlapping? '(["2019-10-08T15:00:00.000Z" "2019-10-08T16:00:00.000Z"]
                                ["2019-10-08T14:00:00.000Z" "2019-10-08T18:00:00.000Z"]))))
    (is (= true (overlapping? '(["2018-10-08T15:00:00.000Z" "2020-10-08T16:00:00.000Z"]
                                ["2019-10-08T14:00:00.000Z" "2019-10-08T18:00:00.000Z"])))))
 (testing "with non-overlapping events"
   (is (= false (overlapping? '(["2019-10-08T15:00:00.000Z" "2019-10-08T16:00:00.000Z"]
                                ["2019-10-08T14:00:00.000Z" "2019-10-08T14:30:00.000Z"])))))
 (testing "with adjacent events"
   (is (= false (overlapping? '(["2019-10-08T15:00:00.000Z" "2019-10-08T16:00:00.000Z"]
                                ["2019-10-08T16:00:00.000Z" "2019-10-08T18:30:00.000Z"]))))))

(deftest pairs-to-csv-test
  (testing "converts to csv"
    (is (= "startA,endA;startB,endB\nstartC,endC;startD,endD"
           (pairs-to-csv '((["startA" "endA"] ["startB" "endB"])
                           (["startC" "endC"] ["startD" "endD"])))))))

(deftest overlapping-events-test
  (testing "returns only overlapping event pairs"
    (is (= '((["2019-10-05" "2019-10-07"]
              ["2019-10-06" "2019-10-07"])
             (["2019-10-05" "2019-10-07"]
              ["2019-10-01" "2019-10-06"]))
           (overlapping-events '(["2019-10-05" "2019-10-07"]
                                 ["2019-10-06" "2019-10-07"]
                                 ["2019-10-08" "2019-10-10"]
                                 ["2019-10-01" "2019-10-06"])))))
  (testing "when no there are no overlapping events"
    (is (= '()
           (overlapping-events '(["2019-10-05" "2019-10-07"]
                                 ["2019-10-15" "2019-10-17"]))))))

(comment
  (clojure.test/run-tests))
