(ns cljdebbugs.main
  (:require
   [cljdebbugs.request :as m.request]
   [clojure.pprint :as pprint])
  (:gen-class))

(def gnu-url "https://debbugs.gnu.org/cgi/soap.cgi?WSDL")

(defn -main [& _args]
  ;; (let [res (m.request/get-bugs gnu-url {:package "emacs"})]
  ;;   (println res))
  (let [res (m.request/get-status gnu-url ["16469" "71284" "57246"])]
    (pprint/pprint res)))
