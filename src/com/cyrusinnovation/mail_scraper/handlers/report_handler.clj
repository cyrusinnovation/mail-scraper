(ns com.cyrusinnovation.mail-scraper.handlers.report-handler
	(:use com.cyrusinnovation.mail-scraper.template-utilities)
  (:require [net.cgrand.enlive-html :as html]))

(defn report [servlet]
	(let [template (prepare-template servlet "report"
																	 [substitution-values]
																	 [:h2.eventTitle] (html/content (:message substitution-values)))
				values-to-substitute {:message "There are no networking events at this time."}]
		(apply str (template values-to-substitute))))
