(ns unit.dispatch-utils-test
	(:use midje.sweet)
	(:use com.cyrusinnovation.mail-scraper.utils.dispatch-utils))

(fact
 (action-name-from ...request...) => "foo"
 (provided (servlet-path-from ...request...) => "foo/"))
