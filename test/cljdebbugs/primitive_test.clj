(ns cljdebbugs.primitive-test
  (:require
   [clojure.test :as t]
   [cljdebbugs.primitive :as sut]
   [clojure.data.xml :as xml]))

(xml/alias-uri 'soap "http://schemas.xmlsoap.org/soap/envelope/")
(xml/alias-uri 'soapenc "http://schemas.xmlsoap.org/soap/encoding/")
(xml/alias-uri 'xsd "http://www.w3.org/2001/XMLSchema")
(xml/alias-uri 'xsi "http://www.w3.org/2001/XMLSchema-instance")
(xml/alias-uri 's "urn:Debbugs/SOAP")

(t/deftest get-status-test
  (t/is (= (xml/sexp-as-element
            [::s/get_status
             [::s/bugs {::xsi/type "soapenc:Array"
                        ::soapenc/arrayType "xsd:int[3]"}
              [::s/bugs {::xsi/type "xsd:int"} 1]
              [::s/bugs {::xsi/type "xsd:int"} 2]
              [::s/bugs {::xsi/type "xsd:int"} 3]]])
           (xml/parse-str
            (sut/render-xml
             (sut/get-status [1 2 3]))))))

(t/deftest get-bugs-test
  (t/is (= (xml/sexp-as-element
            [::s/get_bugs
             [::s/query {::xsi/type "soapenc:Array"
                         ::soapenc/arrayType "xsd:anyType[2]"}
              [::s/query {::xsi/type "xsd:string"} "package"]
              [::s/query {::xsi/type "xsd:string"} "emacs"]]])
           (xml/parse-str
            (sut/render-xml
             (sut/get-bugs {:package "emacs"})))))

  (t/is (= (xml/sexp-as-element
            [::s/get_bugs
             [::s/query {::xsi/type "soapenc:Array"
                         ::soapenc/arrayType "xsd:anyType[4]"}
              [::s/query {::xsi/type "xsd:string"} "package"]
              [::s/query {::xsi/type "xsd:string"} "emacs"]
              [::s/query {::xsi/type "xsd:string"} "severity"]
              [::s/query {::xsi/type "xsd:string"} "normal"]]])
           (xml/parse-str
            (sut/render-xml
             (sut/get-bugs {:package "emacs" :severity "normal"})))))

  (t/is (= (xml/sexp-as-element
            [::s/get_bugs
             [::s/query {::xsi/type "soapenc:Array"
                         ::soapenc/arrayType "xsd:anyType[4]"}
              [::s/query {::xsi/type "xsd:string"} "package"]
              [::s/query {::xsi/type "xsd:string"} "emacs"]
              [::s/query {::xsi/type "xsd:string"} "severity"]
              [::s/query {::xsi/type "xsd:string"} "normal"]]])
           (xml/parse-str
            (sut/render-xml
             (sut/get-bugs {:package "emacs" :severity ["normal"]})))))

  (t/is (= (xml/sexp-as-element
            [::s/get_bugs
             [::s/query {::xsi/type "soapenc:Array"
                         ::soapenc/arrayType "xsd:anyType[6]"}
              [::s/query {::xsi/type "xsd:string"} "package"]
              [::s/query {::xsi/type "xsd:string"} "emacs"]
              [::s/query {::xsi/type "xsd:string"} "severity"]
              [::s/query {::xsi/type "xsd:string"} "normal"]
              [::s/query {::xsi/type "xsd:string"} "severity"]
              [::s/query {::xsi/type "xsd:string"} "important"]]])
           (xml/parse-str
            (sut/render-xml
             (sut/get-bugs {:package "emacs" :severity ["normal" "important"]}))))))

(t/deftest envelop-test
  (t/is (= (xml/sexp-as-element
            [::soap/Envelope
             {::soapenc/encodingStyle "https://schemas.xmlsoap.org/soap/encoding/"}
             [::soap/Body
              {}]])
           (xml/parse-str
            (sut/render-xml
             (sut/envelop
              {}))))))

(t/deftest render-xml-test
  (t/is (= (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                "<foo foo-attr=\"foo value\">"
                "<bar bar-attr=\"bar value\">"
                "<baz baz-attr=\"baz value\">"
                "the baz value"
                "</baz>"
                "</bar>"
                "</foo>")
           (sut/render-xml [:foo {:foo-attr "foo value"}
                                 [:bar {:bar-attr "bar value"}
                                  [:baz {:baz-attr "baz value"}
                                   "the baz value"]]]))))

(t/deftest sexp-hiccup-test
  (t/is (= [:foo]
           (sut/sexp-hiccup
            (xml/sexp-as-element [:foo]))))

  (t/is (= [:foo {:foo-attr "foo value"}]
           (sut/sexp-hiccup
            (xml/sexp-as-element [:foo {:foo-attr "foo value"}]))))

  (t/is (= [:foo {:foo-attr "foo value"}
            [:bar {:bar-attr "bar value"}]]
           (sut/sexp-hiccup
            (xml/sexp-as-element [:foo {:foo-attr "foo value"}
                                  [:bar {:bar-attr "bar value"}]]))))

  (t/is (= [:foo {:foo-attr "foo value"}
            [:bar {:bar-attr "bar value"}
             [:baz {} "The baz value"]]]
           (sut/sexp-hiccup
            (xml/sexp-as-element [:foo {:foo-attr "foo value"}
                                  [:bar {:bar-attr "bar value"}
                                   [:baz {} "The baz value"]]])))))
