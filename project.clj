(defproject worldcup "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-http "3.9.0"]
                 [cheshire "5.8.0"]
                 [proto-repl "0.3.1"]
                 [proto-repl-charts "0.3.2"]]
  :main worldcup.core
  :aot  :all)
