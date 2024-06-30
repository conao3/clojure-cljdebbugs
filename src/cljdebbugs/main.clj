(ns cljdebbugs.main
  (:require
   [clojure.data.xml :as xml]
   [babashka.http-client :as http])
  (:gen-class))

(xml/alias-uri 'soap "http://schemas.xmlsoap.org/soap/envelope/")
(xml/alias-uri 'soapenc "http://schemas.xmlsoap.org/soap/encoding/")
(xml/alias-uri 'xsd "http://www.w3.org/2001/XMLSchema")
(xml/alias-uri 'xsi "http://www.w3.org/2001/XMLSchema-instance")
(xml/alias-uri 't "urn:Debbugs/SOAP")

(defn- render-xml [sexp]
  (xml/indent-str (xml/sexp-as-element sexp)))

(defn -main [& _args]
  (let [body (render-xml
              [::soap/Envelope
               {::soapenc/encodingStyle "https://schemas.xmlsoap.org/soap/encoding/"
                :xmlns/soapenc "http://schemas.xmlsoap.org/soap/encoding/"
                :xmlns/xsd "http://www.w3.org/2001/XMLSchema"
                :xmlns/xsi "http://www.w3.org/2001/XMLSchema-instance"}
               [::soap/Body
                [::t/get_status
                 [::t/bugs {::xsi/type "soapenc:Array"
                            ::soapenc/arrayType "xsd:int[3]"}
                  [::t/bugs {::xsi/type "xsd:int"} 753]
                  [::t/bugs {::xsi/type "xsd:int"} 837]
                  [::t/bugs {::xsi/type "xsd:int"} 841]]]]])
        res (http/post "https://debbugs.gnu.org/cgi/soap.cgi?WSDL"
                       {:headers {:content-type "text/xml"}
                        :body body})]
    (println (:body res))))
