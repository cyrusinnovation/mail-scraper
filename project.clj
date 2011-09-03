(defproject mail-scraper "0.1.0-SNAPSHOT"
	:description  "Scrapes mails sent to Google App Engine, stores and displays the results."
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.1"]
                 [com.google.appengine/appengine-tools-sdk "1.5.3"]
								 [enlive "1.0.0"]]
  :dev-dependencies [[swank-clojure "1.3.2"]
										 [midje "1.1.1"]
										 [lein-midje "1.0.0"]
										 [com.google.appengine/appengine-testing "1.5.1"]
										 [org.seleniumhq.selenium/selenium-java "2.5.0"]]
  :namespaces [com.cyrusinnovation.mail-scraper]
  :compile-path "war/WEB-INF/classes/"
  :library-path "war/WEB-INF/lib/")