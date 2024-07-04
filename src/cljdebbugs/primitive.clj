(ns cljdebbugs.primitive
  (:require
   [clojure.data.xml :as xml]))

(xml/alias-uri 'soap "http://schemas.xmlsoap.org/soap/envelope/")
(xml/alias-uri 'soapenc "http://schemas.xmlsoap.org/soap/encoding/")
(xml/alias-uri 'xsd "http://www.w3.org/2001/XMLSchema")
(xml/alias-uri 'xsi "http://www.w3.org/2001/XMLSchema-instance")
(xml/alias-uri 's "urn:Debbugs/SOAP")

(defn get-bugs [query]
  (let [body (mapcat (fn [[key val]]
                       (mapcat (fn [v] [[::s/query {::xsi/type "xsd:string"} (name key)]
                                        [::s/query {::xsi/type "xsd:string"} v]])
                               (if (coll? val) val [val])))
                     query)]
    `[::s/get_bugs
      [::s/query {::xsi/type "soapenc:Array"
                  ::soapenc/arrayType ~(format "xsd:anyType[%d]" (count body))}
       ~@body]]))

(defn get-status [ids]
  `[::s/get_status
    [::s/bugs {::xsi/type "soapenc:Array"
               ::soapenc/arrayType ~(format "xsd:int[%d]" (count ids))}
     ~@(map (fn [x] [::s/bugs {::xsi/type "xsd:int"} x]) ids)]])

(defn envelop [body]
  `[::soap/Envelope
    {::soapenc/encodingStyle "https://schemas.xmlsoap.org/soap/encoding/"
     :xmlns/soapenc "http://schemas.xmlsoap.org/soap/encoding/"
     :xmlns/xsd "http://www.w3.org/2001/XMLSchema"
     :xmlns/xsi "http://www.w3.org/2001/XMLSchema-instance"}
    [::soap/Body
     ~body]])

(defn render-soap-xml [sexp]
  (xml/emit-str (xml/sexp-as-element sexp)))
