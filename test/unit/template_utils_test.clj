(ns unit.template-utils-test
	(:use midje.sweet)
	(:use com.cyrusinnovation.mail-scraper.utils.template-utils)
  (:require [com.cyrusinnovation.mail-scraper.utils.dispatch-utils :as dispatch-utils]))

(fact
 (template-name "foo") => "foo-template")

(fact
 (template-path "foo") => "/WEB-INF/templates/foo-template.html"
 (provided (template-name "foo") => "foo-template"))

(fact
   (template-file ...servlet... "/WEB-INF/templates/report-template.html") => (new java.io.File "war/WEB-INF/templates/report-template.html")
   (provided (dispatch-utils/real-path-from-context ...servlet... "/WEB-INF/templates/report-template.html") => "war/WEB-INF/templates/report-template.html"))

(fact
 (macroexpand-1 '(prepare-template ...servlet... "action" [substitution-values] [.title] (html/content (:key substitution-values))))
 => '(net.cgrand.enlive-html/deftemplate
       action-template
       (com.cyrusinnovation.mail-scraper.utils.template-utils/template-file ...servlet...
                                                                          (com.cyrusinnovation.mail-scraper.utils.template-utils/template-path "action"))
       [substitution-values]
       [.title] (html/content (:key substitution-values))))
