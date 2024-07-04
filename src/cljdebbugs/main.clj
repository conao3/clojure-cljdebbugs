(ns cljdebbugs.main
  (:require
   [cljdebbugs.request :as m.request])
  (:gen-class))

(defn -main [& _args]
  (let [res (m.request/get-bugs "https://debbugs.gnu.org/cgi/soap.cgi?WSDL" {:package "emacs"})]
    (println (:body res))))
