(ns unit.template-renderer-test
    (:use midje.sweet)
    (:use com.cyrusinnovation.mail-scraper.template-renderer))

(fact
 (action-symbol "foo") => 'com.cyrusinnovation.mail-scraper.handlers.foo-handler/foo)

(fact
 (action "obj-ref" "report") => 6
 (provided
    (com.cyrusinnovation.mail-scraper.handlers.report-handler/report "obj-ref") => 6))

(fact
 (action "obj-ref" (str "report")) => 6
 (provided
    (com.cyrusinnovation.mail-scraper.handlers.report-handler/report "obj-ref") => 6))