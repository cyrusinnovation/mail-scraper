(ns com.cyrusinnovation.mail-scraper.template-renderer
	(:gen-class :extends javax.servlet.http.HttpServlet))

;; Where is it documented that you override a method by
;; preceding its name with a hyphen? Or that the object
;; is the first argument?
(defn -service [this request response]
	(.sendRedirect response "/report.jsp"))