(ns unit.template-utilities-test
	(:use midje.sweet)
	(:use com.cyrusinnovation.mail-scraper.template-utilities))

(fact
 (template-name "foo") => "foo-template")

(fact
 (template-path "foo") => "/WEB-INF/templates/foo-template.html"
 (provided (template-name "foo") => "foo-template"))

(fact
 (action-name-from ...request...) => "foo"
 (provided (servlet-path-from ...request...) => "foo/"))

(fact
   (template-file ...servlet... "/WEB-INF/templates/report-template.html") => (new java.io.File "war/WEB-INF/templates/report-template.html")
   (provided (real-path-from-context ...servlet... "/WEB-INF/templates/report-template.html") => "war/WEB-INF/templates/report-template.html"))

(fact
 (macroexpand-1 '(prepare-template ...servlet... "action" [substitution-values] [.title] (html/content (:key substitution-values))))
 => '(net.cgrand.enlive-html/deftemplate
       action-template
       (com.cyrusinnovation.mail-scraper.template-utilities/template-file ...servlet...
                                                                          (com.cyrusinnovation.mail-scraper.template-utilities/template-path "action"))
       [substitution-values]
       [.title] (html/content (:key substitution-values))))
