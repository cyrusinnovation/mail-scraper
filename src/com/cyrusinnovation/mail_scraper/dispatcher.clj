(ns com.cyrusinnovation.mail-scraper.dispatcher
  ;; You need to include yur handlers here, not just drop them in the handlers directory.
  (:require com.cyrusinnovation.mail-scraper.handlers.report-handler)
  (:require com.cyrusinnovation.mail-scraper.handlers.events-handler)
  (:require [com.cyrusinnovation.mail-scraper.utils.dispatch-utils :as utils])
  (:gen-class :extends javax.servlet.http.HttpServlet))

(import javax.servlet.http.HttpServletResponse)

(defn action-symbol [action-name]
  (symbol (str "com.cyrusinnovation.mail-scraper.handlers." action-name "-handler/" action-name)))

(defmacro dispatch [action-name servlet request response]
  `((eval (action-symbol ~action-name)) ~servlet ~request ~response))

(defn set-body [response html]
    (let [writer (.getWriter response)]
        (.write writer html)))

(defn -service [this request response]
  (set-body response
            (dispatch (utils/action-name-from request) this request response))
    (.setStatus response HttpServletResponse/SC_OK))