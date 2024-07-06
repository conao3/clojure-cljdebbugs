(ns cljdebbugs.request-test
  (:require
   [clojure.test :as t]
   [cljdebbugs.primitive :as m.primitive]
   [cljdebbugs.request :as sut]
   [clojure.data.xml :as xml]))

(xml/alias-uri 'soap "http://schemas.xmlsoap.org/soap/envelope/")
(xml/alias-uri 'soapenc "http://schemas.xmlsoap.org/soap/encoding/")
(xml/alias-uri 'xsd "http://www.w3.org/2001/XMLSchema")
(xml/alias-uri 'xsi "http://www.w3.org/2001/XMLSchema-instance")
(xml/alias-uri 's "urn:Debbugs/SOAP")

(def url "https://sample.com/soap")

(t/deftest get-bugs-test
  (with-redefs [sut/request (constantly {:status 200
                                         :body (->> [::s/get_bugsResponse
                                                     {::xsi/type "soapenc:Array"
                                                      ::soapenc/Array {::soapenc/arrayType "xsd:int[3]"}}
                                                     [::s/item {::xsi/type "xsd:int"} "16469"]
                                                     [::s/item {::xsi/type "xsd:int"} "71284"]
                                                     [::s/item {::xsi/type "xsd:int"} "57246"]]
                                                    m.primitive/envelop
                                                    xml/sexp-as-element
                                                    xml/emit-str)})]
    (t/is (= ["16469" "71284" "57246"]
             (sut/get-bugs url {:package "emacs"})))))

(t/deftest get-status-test
  (with-redefs [sut/request (constantly {:status 200
                                         :body (->> [::s/get_statusResponse
                                                     [::s/s-gensym3 {::xsi/type "apachens:Map"}
                                                      [::s/item
                                                       [::s/key {::xsi/type "xsd:int"} "16469"]
                                                       [::s/value
                                                        [::s/bug_num {::xsi/type "xsd:int"} "16469"]
                                                        [::s/forwarded {::xsi/type "xsd:string"}]
                                                        [::s/found_versions {::xsi/type "soapenc:Array" ::soapenc/arrayType "xsd:string[2]"}
                                                         [::s/item {::xsi/type "xsd:string"} "24.3.50"]
                                                         [::s/item {::xsi/type "xsd:string"} "28.0.50"]]
                                                        [::s/found {::xsi/type "apachens:Map"}
                                                         [::s/item
                                                          [::s/key {::xsi/type "xsd:str"} "24.3.50"]
                                                          [::s/value {::xsi/nil "true"}]]
                                                         [::s/item
                                                          [::s/key {::xsi/type "xsd:str"} "28.0.50"]
                                                          [::s/value {::xsi/nil "true"}]]]]]
                                                      [::s/item
                                                       [::s/key {::xsi/type "xsd:int"} "71284"]
                                                       [::s/value
                                                        [::s/bug_num {::xsi/type "xsd:int"} "71284"]
                                                        [::s/forwarded {::xsi/type "xsd:string"}]]]]]
                                                    m.primitive/envelop
                                                    xml/sexp-as-element
                                                    xml/emit-str)})]
    (t/is (= [{:bug_num ["16469"]
               :forwarded []
               :found_versions ["24.3.50" "28.0.50"]
               :found {"24.3.50" true "28.0.50" true}}
              {:bug_num ["71284"]
               :forwarded []}]
             (sut/get-status url ["16469" "71284" "57246"])))))
