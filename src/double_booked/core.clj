(ns double-booked.core
  (:gen-class)
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]
            [clojure.string :as str]
            [clojure.math.combinatorics :as comb]))

(defn parse-csv [data]
  (->> (str/split data #";")
       (map (fn [line] (->> (str/split line #",")
                            (map str/trim))))))

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
  "Takes in a sequence of events, each having a start and end time,
  and returns to stdout the sequence of all pairs of overlapping events.

  Input events should be defined as a pair of start-datetime and end-datetime separated by `,`:
  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z
  ```
  Events, in a sequence, should be separated by `;`:
  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z; 2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z;
  ```

  Output is printed to stdout as pairs of overlapping events.
  Each pair is separated by newline.
  Each event in a pair is separated by `;`.
  Event's start-datetime and end-datetime are separated by `,`.

  For example, running:
  ```
  lein run '2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z; 2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z; 2019-10-08T11:00:00.000Z,2019-10-08T12:00:00.000Z; 2019-10-08T11:30:00.000Z, 2019-10-08T15:10:00.000Z'
  ```
  outputs:
  ```
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z\n
  2019-10-08T15:00:00.000Z,2019-10-08T16:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z\n
  2019-10-08T14:00:00.000Z,2019-10-08T18:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z\n
  2019-10-08T11:00:00.000Z,2019-10-08T12:00:00.000Z;2019-10-08T11:30:00.000Z,2019-10-08T15:10:00.000Z
  ```"
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
