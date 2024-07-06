(ns cljdebbugs.request
  (:require
   [babashka.http-client :as http]
   [cljdebbugs.primitive :as m.primitive]
   [clojure.data.xml :as xml]))

(defn request [url xml-sexp]
  (http/post url {:headers {:content-type "text/xml"}
                  :body (m.primitive/render-xml xml-sexp)}))

(defn get-bugs [url query]
  (let [res (request url (m.primitive/envelop
                          (m.primitive/get-bugs query)))]
    {:result res
     :body (xml/parse-str (:body res))
     :content (->> res
                   :body
                   :content first
                   :content first
                   :content first
                   :content (map (comp first :content)))}))

(defn get-status [url ids]
  (request url (m.primitive/envelop
                (m.primitive/get-status ids))))
