(ns cljdebbugs.primitive
  (:require
   [clojure.data.xml :as xml]))

(xml/alias-uri 'soap "http://schemas.xmlsoap.org/soap/envelope/")
(xml/alias-uri 'soapenc "http://schemas.xmlsoap.org/soap/encoding/")
(xml/alias-uri 'xsd "http://www.w3.org/2001/XMLSchema")
(xml/alias-uri 'xsi "http://www.w3.org/2001/XMLSchema-instance")
(xml/alias-uri 't "urn:Debbugs/SOAP")

(defn get-status [ids]
  `[::t/get_status
    [::t/bugs {::xsi/type "soapenc:Array"
               ::soapenc/arrayType ~(format "xsd:int[%d]" (count ids))}
     ~@(map (fn [x] [::t/bugs {::xsi/type "xsd:int"} x]) ids)]])

(defn get-bugs [query]
  `[::t/get_bugs
    [::t/query {::xsi/type "soapenc:Array"
                ::soapenc/arrayType ~(format "xsd:anyType[%d]" (* (count query) 2))}
     ~@(mapcat (fn [[key val]]
                 (mapcat (fn [v] [[::t/query {::xsi/type "xsd:string"} (name key)]
                                  [::t/query {::xsi/type "xsd:string"} v]])
                         (if (coll? val) val [val])))
               query)]])

(defn- envelop [body]
  `[::soap/Envelope
    {::soapenc/encodingStyle "https://schemas.xmlsoap.org/soap/encoding/"
     :xmlns/soapenc "http://schemas.xmlsoap.org/soap/encoding/"
     :xmlns/xsd "http://www.w3.org/2001/XMLSchema"
     :xmlns/xsi "http://www.w3.org/2001/XMLSchema-instance"}
    [::soap/Body
     ~body]])

(defn render-soap-xml [sexp]
  (xml/indent-str (xml/sexp-as-element (envelop sexp))))
