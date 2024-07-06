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

(defn- parse-status-item [sexp]
  (->> sexp
       :content second
       :content
       (mapcat #(list (keyword (name (:tag %)))
                      (cond (= (->> % :attrs ::xsi/type) "soapenc:Array")
                            (->> % :content (mapcat :content))
                            (= (->> % :attrs ::xsi/type) "apachens:Map")
                            (->> % :content
                                 (mapcat (fn [x] (->> x :content
                                                      ((fn [y] (list
                                                                (->> y first :content first)
                                                                (= "true" (->> y second :attrs ::xsi/nil))))))))
                                 (apply array-map))
                            :else (:content %))))
       (apply array-map)))

(defn get-status [url ids]
  (->> (->> ids m.primitive/get-status m.primitive/envelop)
       (request url)
       :body
       xml/parse-str
       xml-seq
       (filter #(= (:tag %) ::s/get_statusResponse)) first
       :content first                   ; skip s/s-gensym3 tag
       :content
       (map parse-status-item)))
