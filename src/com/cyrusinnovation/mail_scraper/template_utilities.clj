(ns com.cyrusinnovation.mail-scraper.template-utilities
	(:require [clojure.string :as str])
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
	(str/replace (servlet-path-from request) "/" ""))

(defmacro prepare-template [servlet template-name template-path template-signature & template-mapping-forms]
		`(html/deftemplate ~(symbol (eval template-name)) (new java.io.File (real-path-from-context ~servlet ~(eval template-path)))
			~template-signature ~@template-mapping-forms))
