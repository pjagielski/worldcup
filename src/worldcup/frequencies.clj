(ns worldcup.frequencies
  (:require
    [worldcup.matches :as match]
    [proto-repl-charts.charts :as charts]
    [proto-repl.saved-values :as saved]))

(def all-matches
  (match/get-all-matches))

(def example-match
  (-> all-matches
    first))

(defn completed? [match]
  (= "completed" (:status match)))

(comment
  (completed? example-match))

(defn result [match]
  (let [home-goals (->> match :home_team :goals)
        away-goals (->> match :away_team :goals)]
    (str (max home-goals away-goals) "-" (min home-goals away-goals))))

(comment
  (result {:home_team {:goals 3} :away_team {:goals 2}})
  (result {:home_team {:goals 2} :away_team {:goals 4}}))

(defn results-frequencies []
  (->> all-matches
    (filter completed?)
    (map result)
    (frequencies)
    (sort-by second)
    (reverse)))

(comment
  (results-frequencies)
  (charts/custom-chart
    "World cup results"
    {:data {:columns (results-frequencies)
            :type "pie"}}))
