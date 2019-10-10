(ns double-booked.core
  (:gen-class)
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.string :as str]
            [clojure.math.combinatorics :as comb]))

(defn parse-csv [data]
  (when-let [lines (str/split data #";")]
    (map  (fn [line] (->> (str/split line #",")
                          (map str/trim)))
          lines)))

(defn pairs-to-csv [events]
  (->> events
       (map (fn [pair] (->> pair
                            (map (fn [event] (str/join "," event)))
                            (str/join ";"))))
       (str/join "\n")))

(defn overlapping? [events]
  (->> events
    (map (fn [event] (mapv c/from-string event)))
    (map (fn [event] (apply t/interval event)))
    (apply t/overlaps?)))

(defn overlapping-events [events]
  (->> (comb/combinations events 2)
       (filter overlapping?)))

(defn -main
  [& args]
  (some-> args
          first
          parse-csv
          overlapping-events
          pairs-to-csv
          println))

(comment
  (def csv-input
    (str "2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;"
         "2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z;"
         "2019-10-08T11:00:00.000Z,2019-10-08T12:00:00.000Z;"
         "2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z"))

  (def input-data-1
    '(["2019-10-08T15:00:00.000Z" "2019-10-08T16:00:00.000Z"]
      ["2019-10-08T14:00:00.000Z" "2019-10-08T18:00:00.000Z"]
      ["2019-10-08T11:00:00.000Z" "2019-10-08T12:00:00.000Z"]
      ["2019-10-08T11:30:00.000Z" "2019-10-08T15:10:00.000Z"])))

(comment
  (let [intervals (->> input-data-1
                      (map (fn [pair] (mapv c/from-string pair)))
                      (map (fn [[start end]] (t/interval start end))))
        combinations (comb/combinations intervals 2)]
    (filter (fn [[e1 e2]] (t/overlaps? e1 e2)) combinations)))
