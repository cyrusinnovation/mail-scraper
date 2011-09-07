(let [appserver-version "1.5.3"]

(defproject mail-scraper "0.1.0-SNAPSHOT"
	:description  "Scrapes mails sent to Google App Engine, stores and displays the results."
	
	:repositories {"sonatype-releases" "https://oss.sonatype.org/content/repositories/releases"
								 "sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"}

  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
								 [javax.servlet/servlet-api "2.5"]
                 [com.google.appengine/appengine-tools-sdk ~appserver-version]
								 [enlive "1.0.0"]]

	;; Add lein-gae-serverctl and lein-gae-uat when pluginized, and remove scripts from leiningen directory
  :dev-dependencies [[midje "1.2.0"]
										 [lein-midje "1.0.3"] 
										 [org.seleniumhq.selenium/selenium-java "2.5.0"]
										 [com.google.appengine/appengine-testing ~appserver-version]]
	
  :aot [com.cyrusinnovation.mail-scraper.template-renderer]
  :compile-path "war/WEB-INF/classes/"
  :library-path "war/WEB-INF/lib/"
		
	:gae-appserver-sdk-install-path ~(str "/usr/local/appengine-java-sdk-" appserver-version)
	:gae-appserver-address "localhost"
	:gae-appserver-port "8080"
  ))

;; Running lein test will now start the server beforehand and stop it afterwards.
;; In addition it will use lein-midje to run the tests, to generate better reports.
(require '(leiningen test [gae :as server] [midje :as midje]))
(add-hook #'leiningen.test/test
          (fn [test project & args]
            (server/dev-appserver project)
            (midje/midje project)
            (server/kill-appserver)))
