(ns leiningen.uat
	(use [leiningen.gae :only [dev-appserver kill-appserver]]
			 [leiningen.midje]))

(defn uat [project & args]
	(dev-appserver project)
	;; Make this run all the tests in endtoend
	(midje project "endtoend.report-end-to-end-test")
	(kill-appserver))
