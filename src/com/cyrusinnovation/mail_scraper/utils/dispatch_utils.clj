(ns com.cyrusinnovation.mail-scraper.utils.dispatch-utils
	(:require [clojure.string :as string]))

(defn servlet-path-from [request]
	(.getServletPath request))

(defn real-path-from-context [servlet path]
	(.getRealPath (.getServletContext servlet) path))

(defn action-name-from [request]
	(string/replace (servlet-path-from request) "/" ""))
