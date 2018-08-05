(ns worldcup.matches
  (:require
    [clj-http.client :as http]))

(def api-root "https://worldcup.sfg.io")

(defn get-all-matches []
  (->
    (http/get (str api-root "/matches") {:as :json})
    :body))
