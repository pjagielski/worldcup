(ns worldcup.comeback
  (:require
    [clojure.string :as string]
    [worldcup.matches :as match]))

(def all-matches
  (match/get-all-matches))

(comment
  (->> all-matches
    (mapcat #(concat (:home_team_events %) (:away_team_events %)))
    (map :type_of_event)
    (set)))

(defn goal? [e]
  (let [type (:type_of_event e)]
    (or (= "goal" type)
        (= "goal-penalty" type)
        (= "goal-own" type))))

(comment
  (->> all-matches
    first
    :home_team_events
    (filter goal?)))

(defn goal-time [t]
  (->>
    (string/split t #"\+")
    (map string/trim)
    (map #(string/replace % #"'" ""))
    (map #(Integer/parseInt %))
    (reduce +)))

(comment
  (goal-time "90' + 7'")
  (goal-time "43'"))

(comment
  (->> all-matches
    first
    :home_team_events
    (filter goal?)
    (map :time)
    (map goal-time)))

(defn teams [match]
  (select-keys match [:home_team_country :away_team_country :winner]))

(defn team-goal-times [events side]
  (->> events
    (filter goal?)
    (map :time)
    (map goal-time)
    (map vector (repeat side))))

(comment
  (->>
    (repeat :home)
    (take 10))
  (->>
    (vector :a 1)))


(comment
  (->>
    [1 2 3]
    (map + [2 4 6])))


(comment
  (->>
    [:a :b :c]
    (map vector (repeat 1)))

  (->>
    [43 55 66]
    (map vector (repeat :home))))

(defn winner-side [match]
  (let [winner (:winner match)]
    (cond
      (= winner (:home_team_country match)) :home
      (= winner (:away_team_country match)) :away)))

(defn first-scored-side [match]
  (let [goal-times
          (concat
            (team-goal-times (:home_team_events match) :home)
            (team-goal-times (:away_team_events match) :away))]
   (->> goal-times (sort-by second) first first)))

(comment
  (first [[:away 28] [:home 38] [:home 39] [:home 59] [:home 65]])
  (first [:away 28]))

(comment
  (->> all-matches
    (map first-scored-side)))

(defn come-back? [match]
  (let [first-scored-side (first-scored-side match)
        winner-side (winner-side match)]
    (and (not (nil? winner-side))
         (not= winner-side first-scored-side))))

(comment
  (->> all-matches
    (filter come-back?)
    (map teams)))

(def match-to-fix
  (->> all-matches
    (filter come-back?)
    first))

(comment
  (->
    {:foo [1 2 3] :bar 2}
    (update :foo #(map inc %)))
  (->
    {:foo {:bar {:buz [1 2 3]}}}
    (update-in [:foo :bar :buz] #(map inc %))))

(defn own-goal? [event]
  (= "goal-own" (:type_of_event event)))

(defn own-goals [events]
  (->> events (filter own-goal?)))

(comment
  (-> match-to-fix)
  (own-goals (:home_team_events match-to-fix)))

(defn remove-own-goals [events]
  (remove own-goal? events))

(defn fix-match [match-to-fix]
  (let [own-home-goals (own-goals (:home_team_events match-to-fix))
        own-away-goals (own-goals (:away_team_events match-to-fix))]
   (-> match-to-fix
     (update :home_team_events remove-own-goals)
     (update :away_team_events remove-own-goals)
     (update :home_team_events concat own-away-goals)
     (update :away_team_events concat own-home-goals))))

(def fixed-match
  (fix-match match-to-fix))

(defn come-back? [match-to-fix]
  (let [match (fix-match match-to-fix)
        first-scored-side (first-scored-side match)
        winner-side (winner-side match)]
    (and (not (nil? winner-side))
         (not= winner-side first-scored-side))))

(comment
  (winner-side fixed-match)
  (->>
    (team-goal-times (:home_team_events fixed-match) :home)
    (sort-by second)
    first
    first))

(comment
  (-> example-match)
  (->>
    all-matches
    (filter come-back?)
    (map teams))

  (first-goal (:home_team_events (fix-match match-to-fix))))
