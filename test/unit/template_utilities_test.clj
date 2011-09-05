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
 (macroexpand-1 '(prepare-template "obj" "template-name" "template-path" [substitution-values] [.title] (html/content (:key substitution-values))))
 => '(html/deftemplate template-name
			 (new java.io.File (com.cyrusinnovation.networkingevents.template-utilities/real-path-from-context "obj" "template-path"))
											[substitution-values] [.title] (html/content (:key substitution-values))))

(fact
 (macroexpand-1 '(prepare-template "obj" (template-name "foo") (template-path "foo") [subs-values] [.title] (html/content (:key subs-values))))
 => '(html/deftemplate foo-template
			 (new java.io.File (com.cyrusinnovation.networkingevents.template-utilities/real-path-from-context "obj" "/WEB-INF/templates/foo-template.html"))
											[subs-values] [.title] (html/content (:key subs-values))))