(ns unit.template-utils-test
	(:use midje.sweet)
	(:use com.cyrusinnovation.mail-scraper.utils.template-utils)
  (:require [net.cgrand.enlive-html :as html])
  (:require [com.cyrusinnovation.mail-scraper.utils.dispatch-utils :as dispatch-utils]))

(fact "The correct template name is generated"
 (template-name "foo") => "foo-template")

(fact "Templates are found under the proper directory"
 (template-path "foo") => "/WEB-INF/templates/foo-template.html"
 (provided (template-name "foo") => "foo-template"))

(fact "The template-file function creates the right file."
   (template-file ...servlet... "/WEB-INF/templates/report-template.html") => (new java.io.File "war/WEB-INF/templates/report-template.html")
   (provided (dispatch-utils/real-path-from-context ...servlet... "/WEB-INF/templates/report-template.html") => "war/WEB-INF/templates/report-template.html"))

(fact "The prepare-template macro expands properly."
 (macroexpand-1 '(prepare-template ...servlet... "action" [substitution-values] [:title] (html/content (:key substitution-values))))
 => '(clojure.core/or (clojure.core/resolve (clojure.core/symbol (com.cyrusinnovation.mail-scraper.utils.template-utils/template-name "action")))
                      (net.cgrand.enlive-html/deftemplate
                        action-template
                        (com.cyrusinnovation.mail-scraper.utils.template-utils/template-file
                                ...servlet...
                                (com.cyrusinnovation.mail-scraper.utils.template-utils/template-path "action"))
                        [substitution-values]
                        [:title] (html/content (:key substitution-values)))))

(fact "if a template doesn't exist, it is created"
      (against-background (before :facts
                                  (ns-unmap 'unit.template-utils-test 'action-template)))
            (resolve 'action-template) => nil
            (:name (meta (prepare-template ...servlet... "action" [vals] [:title] (html/content (:title vals))))) => 'action-template)

(fact "if a template does exist, it is not recreated"
      (against-background (before :facts
                                  (ns-unmap 'unit.template-utils-test 'action-template)))
      (let [first (.hashCode (prepare-template ...servlet... "action" [vals] [:title] (html/content (:title vals))))]
        (.hashCode (prepare-template ...servlet... "action" [vars] [:something_else] (html/content (:something_else vars)))) => first))


(fact "if a template is recreated, it gets a different hash code"
      (against-background (before :facts
                                  (ns-unmap 'unit.template-utils-test 'action-template)))
      (let [first (.hashCode (prepare-template ...servlet... "action" [vals] [:title] (html/content (:title vals))))]
        (ns-unmap 'unit.template-utils-test 'action-template)
        (= first (.hashCode (prepare-template ...servlet... "action" [vars] [:something_else] (html/content (:something_else vars))))) => false))
  