(ns cljdebbugs.request
  (:require
   [babashka.http-client :as http]
   [cljdebbugs.primitive :as m.primitive]
   [clojure.data.xml :as xml]
   [clojure.data.xml.tree :as xml.tree]))

(xml/alias-uri 'soap "http://schemas.xmlsoap.org/soap/envelope/")
(xml/alias-uri 'soapenc "http://schemas.xmlsoap.org/soap/encoding/")
(xml/alias-uri 'xsd "http://www.w3.org/2001/XMLSchema")
(xml/alias-uri 'xsi "http://www.w3.org/2001/XMLSchema-instance")
(xml/alias-uri 's "urn:Debbugs/SOAP")

(defn request [url xml-sexp]
  (http/post url {:headers {:content-type "text/xml"}
                  :body (m.primitive/render-xml xml-sexp)}))

(defn get-bugs [url query]
  (->> (->> query m.primitive/get-bugs m.primitive/envelop)
       (request url)
       :body
       xml/parse-str
       xml-seq
       (filter #(= (:tag %) ::s/item))
       (map (comp first :content))))

(defn get-status [url ids]
  (request url (m.primitive/envelop
                (m.primitive/get-status ids))))
