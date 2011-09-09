(ns com.cyrusinnovation.mail-scraper.template-renderer
    (:use com.cyrusinnovation.mail-scraper.template-utilities)
    (:use com.cyrusinnovation.mail-scraper.handlers.report-handler)
    (:gen-class :extends javax.servlet.http.HttpServlet))

(import javax.servlet.http.HttpServletResponse)

(defn action-symbol [action-name]
  (symbol (str "com.cyrusinnovation.mail-scraper.handlers." action-name "-handler/" action-name)))

(defmacro dispatch [this action-name]
  `((eval (action-symbol ~action-name)) ~this))

(defn set-body [response html]
    (let [writer (.getWriter response)]
        (.write writer html)))

(defn -service [this request response]
  (set-body response
            (dispatch this (action-name-from request)))
    (.setStatus response HttpServletResponse/SC_OK))