(ns double-booked.core
  (:gen-class)
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.math.combinatorics :as comb]))


;; Assumption 1
;; Input data is a list of vectors pairs of string dates
;;
;; Motivation string dates can be passed via argv or http
;; less dependent on source encoding

(def input-data-1
  '(["2019-10-08T15:00:00.000Z" "2019-10-08T16:00:00.000Z"]
    ["2019-10-08T14:00:00.000Z" "2019-10-08T18:00:00.000Z"]
    ["2019-10-08T11:00:00.000Z" "2019-10-08T12:00:00.000Z"]
    ["2019-10-08T11:30:00.000Z" "2019-10-08T15:10:00.000Z"]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(comment
  (let [intervals (->> input-data-1
                      (map (fn [pair] (mapv c/from-string pair)))
                      (map (fn [[start end]] (t/interval start end))))
        combinations (comb/combinations intervals 2)]
    (filter (fn [[e1 e2]] (t/overlaps? e1 e2)) combinations)))
