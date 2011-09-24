(ns unit.events-handler-tests
  (:use midje.sweet)
  (:require [com.cyrusinnovation.mail-scraper.handlers.events-handler :as handler])
  (:require appengine-clj.datastore))

(import '(com.google.appengine.tools.development.testing LocalServiceTestHelper LocalServiceTestConfig LocalDatastoreServiceTestConfig))
(import '(com.google.appengine.api.datastore DatastoreServiceFactory FetchOptions))
(import '(com.google.appengine.api.datastore Entity Query Text))
(import java.io.FileInputStream)
(import java.util.Properties)
(import javax.mail.Session)
(import javax.mail.internet.MimeMessage)

(background (around :facts (let [config (new LocalDatastoreServiceTestConfig)
                                 helper (new LocalServiceTestHelper (into-array LocalServiceTestConfig [config]))]
                             (.setUp helper)
                             ?form
                             (.tearDown helper))))

;; Works at the command line; doesn't seem to work with midje-mode.
(fact "We can store long text to the data store"
  (let [data-store (DatastoreServiceFactory/getDatastoreService)
        entity (new Entity "Event")
        msg-text (slurp "./test/data/startup-digest-mail.txt")]
    (.setProperty entity "msg-text" (new Text msg-text))
    (.setProperty entity "title" "foo")
    (.put data-store entity)
    (-> (.prepare data-store (new Query "Event")) (.countEntities (com.google.appengine.api.datastore.FetchOptions$Builder/withLimit 10))) => 1
    (let [key (.getKey entity)
          result (.get data-store key)]
      (-> (.getProperty result "msg-text") (.getValue)) => msg-text)))

(fact "We can store long text to the data store using the app_datastore API"
  (let [msg-text (slurp "./test/data/startup-digest-mail.txt")]
    (appengine-clj.datastore/create {:kind "Event" :title "foo" :msg-text (new com.google.appengine.api.datastore.Text msg-text)})
    (let [result (appengine-clj.datastore/find-all (Query. "Event"))]
      (count result) => 1
      (:title (first result)) => "foo"
      (-> (first result) (:msg-text) (.getValue)) => msg-text)))
 
;; (fact "We can store the from, date, text, and HTML of a MIME message"
;;   (let [file-input-stream (new FileInputStream "./test/data/startup-digest-mail.txt")
;;         session (Session/getDefaultInstance (new Properties))
;;         mime-message  (new MimeMessage session file-input-stream)]
;;     (process-mail mime-message)
;;     (let [result (appengine-clj.datastore/find-all (Query. "Event"))
;;           record (first result)]
;;       (count result) => 1
;;       (:from record) => (from-address mime-message)
;;       (:date record) => (sent-date mime-message)
;;       (-> (:mail-message-text record) (.getValue)) => (plain-text-content mime-message)
;;       (-> (:mail-message-html record) (.getValue)) => (html-content mime-message)))))
    