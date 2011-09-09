(ns com.cyrusinnovation.mail-scraper.utils.template-utils
  (:use com.cyrusinnovation.mail-scraper.utils.dispatch-utils)
  (:require [net.cgrand.enlive-html :as html]))

(defn template-name [action-name]
	(str action-name "-template"))

(defn template-path [action-name]
	(str "/WEB-INF/templates/" (template-name action-name) ".html"))

(defn template-file [servlet template-path]
  (new java.io.File (real-path-from-context servlet template-path)))

(defmacro prepare-template [servlet action-name template-signature & template-mapping-forms]
  `(or (resolve (symbol (template-name ~action-name)))
       (html/deftemplate
         ~(symbol (template-name action-name))
         (template-file ~servlet (template-path ~action-name))
         ~template-signature
         ~@template-mapping-forms)))
