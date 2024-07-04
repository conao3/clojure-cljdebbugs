(ns cljdebbugs.request
  (:require
   [babashka.http-client :as http]
   [cljdebbugs.primitive :as m.primitive]))

(defn request [url xml-sexp]
  (http/post url {:headers {:content-type "text/xml"}
                  :body (m.primitive/render-soap-xml xml-sexp)}))

(defn get-bugs [url query]
  (request url (m.primitive/envelop
                (m.primitive/get-bugs query))))

(defn get-status [url ids]
  (request url (m.primitive/envelop
                (m.primitive/get-status ids))))
