(ns com.cyrusinnovation.mail-scraper.handlers.report-handler
	(:require [com.cyrusinnovation.mail-scraper.utils.template-utils :as utils])
  (:require [net.cgrand.enlive-html :as html]))

(defn report [servlet request response]
	(let [template (utils/prepare-template servlet "report"
																	 [substitution-values]
                                   [:title] (html/content (:title substitution-values))
																	 [:h2.eventTitle] (html/content (:message substitution-values)))
				values-to-substitute {:title "Networking Events"
                              :message "There are no networking events at this time."}]
		(apply str (template values-to-substitute))))
