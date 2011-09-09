(ns unit.template-renderer-test
    (:use midje.sweet)
    (:use com.cyrusinnovation.mail-scraper.template-renderer))

(fact
 (action-symbol "foo") => 'com.cyrusinnovation.mail-scraper.handlers.foo-handler/foo)

(fact
 (dispatch :some-obj "report") => 6
 (provided
    (com.cyrusinnovation.mail-scraper.handlers.report-handler/report :some-obj) => 6))

(fact
 (dispatch :some-obj (str "report")) => 6
 (provided
    (com.cyrusinnovation.mail-scraper.handlers.report-handler/report :some-obj) => 6))