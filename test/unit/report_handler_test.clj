(ns unit.mail-scraper.report-handler-test
    (:use midje.sweet)
    (:require [com.cyrusinnovation.mail-scraper.template-utilities :as utils])
    (:require [com.cyrusinnovation.mail-scraper.handlers.report-handler :as handler]))

(fact
 (handler/report ...servlet...) => "<html>\n<head><title>Networking Events</title></head>\n</html>"
 (provided
    (utils/real-path-from-context ...servlet... "/WEB-INF/templates/report-template.html") => "test/unit/test-template.html"))