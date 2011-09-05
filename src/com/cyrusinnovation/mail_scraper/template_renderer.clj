(ns com.cyrusinnovation.mail-scraper.template-renderer
	(:require [net.cgrand.enlive-html :as html] [clojure.string :as str])
	(:gen-class :extends javax.servlet.http.HttpServlet))

(import javax.servlet.http.HttpServletResponse)

(defmacro prepare-template [this template-name template-path template-signature & template-mapping-forms]
	`(html/deftemplate
		 ~(symbol template-name)
		 (new java.io.File (.getRealPath (.getServletContext ~this) ~template-path))
		 ~template-signature
		 ~@template-mapping-forms))

(defn render-template [this template-name values-to-substitute]
	(let [template-path (str  "/WEB-INF/templates/" template-name ".html")
				template (prepare-template this template-name template-path
																	 [substitution-values]
																	 [:h2.eventTitle] (html/content (:message substitution-values)))]
        (apply str (template values-to-substitute))))

(defn set-body [response html]
    (let [writer (.getWriter response)]
        (.write writer html)))

(defn -service [this request response]
	(let [template-name (str (str/replace (.getServletPath request) "/" "") "-template")]
		(set-body response (render-template this template-name {:message "There are no networking events at this time."}))
		(.setStatus response HttpServletResponse/SC_OK)))
