(ns unit.dispatcher-test
    (:use midje.sweet)
    (:use com.cyrusinnovation.mail-scraper.dispatcher))

(fact
 (action-symbol "foo") => 'com.cyrusinnovation.mail-scraper.handlers.foo-handler/foo)

(fact
 (dispatch "report" :dummy-servlet :dummy-request :dummy-response) => 6
 (provided
    (com.cyrusinnovation.mail-scraper.handlers.report-handler/report :dummy-servlet :dummy-request :dummy-response) => 6))

(fact
 (dispatch (str "report") :dummy-servlet :dummy-request :dummy-response) => 6
 (provided
    (com.cyrusinnovation.mail-scraper.handlers.report-handler/report :dummy-servlet :dummy-request :dummy-response) => 6))