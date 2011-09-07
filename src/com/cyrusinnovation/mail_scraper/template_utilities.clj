(ns com.cyrusinnovation.mail-scraper.template-utilities
	(:require [clojure.string :as string])
  (:require [net.cgrand.enlive-html :as html]))

(defn template-name [action-name]
	(str action-name "-template"))

(defn template-path [action-name]
	(str "/WEB-INF/templates/" (template-name action-name) ".html"))

(defn servlet-path-from [request]
	(.getServletPath request))

(defn real-path-from-context [servlet path]
	(.getRealPath (.getServletContext servlet) path))

(defn action-name-from [request]
	(string/replace (servlet-path-from request) "/" ""))

(defn template-file [servlet template-path]
  (new java.io.File (real-path-from-context servlet template-path)))

(defmacro prepare-template [servlet action-name template-signature & template-mapping-forms]
  `(html/deftemplate
     ~(symbol (template-name action-name))
     (template-file ~servlet (template-path ~action-name))
		 ~template-signature
     ~@template-mapping-forms))
