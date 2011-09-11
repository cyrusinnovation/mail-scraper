(let [appserver-version "1.5.3"]

(defproject mail-scraper "0.1.0-SNAPSHOT"
	:description  "Scrapes mails sent to Google App Engine, stores and displays the results."
	
	:repositories {"sonatype-releases" "https://oss.sonatype.org/content/repositories/releases"
								 "sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots"}

  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
								 [javax.servlet/servlet-api "2.5"]
                 [com.google.appengine/appengine-tools-sdk ~appserver-version]
                 [com.google.appengine/appengine-api-1.0-sdk ~appserver-version]
								 [enlive "1.0.0"]]

	;; Add lein-gae-serverctl when pluginized, and remove script from leiningen directory
  :dev-dependencies [[midje "1.2.0"]
										 [lein-midje "1.0.3"] 
										 [org.seleniumhq.selenium/selenium-java "2.5.0"]
										 [com.google.appengine/appengine-testing ~appserver-version]
                     [com.google.appengine/appengine-api-stubs ~appserver-version]]
	
  :aot [com.cyrusinnovation.mail-scraper.dispatcher com.cyrusinnovation.mail-scraper.handlers.report-handler com.cyrusinnovation.mail-scraper.handlers.events-handler]
  :compile-path "war/WEB-INF/classes/"
  :library-path "war/WEB-INF/lib/"
  
	:hooks [leiningen.hooks.selenium-test-hook]
  :implicit-hooks false
	:gae-appserver-sdk-install-path ~(str "/usr/local/appengine-java-sdk-" appserver-version)
	:gae-appserver-address "localhost"
	:gae-appserver-port "8080"
  ))

