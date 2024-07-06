(ns cljdebbugs.main
  (:require
   [cljdebbugs.request :as m.request])
  (:gen-class))

(def gnu-url "https://debbugs.gnu.org/cgi/soap.cgi?WSDL")

(defn -main [& _args]
  (let [res (m.request/get-bugs gnu-url {:package "emacs"})]
    (println res)))
