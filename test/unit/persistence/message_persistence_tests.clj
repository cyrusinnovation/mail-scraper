(ns unit.persistence.message-persistence-tests
  (:use midje.sweet)
  (:require [com.cyrusinnovation.mail-scraper.persistence.message-persistence :as persister])
  (:require [com.cyrusinnovation.mail-scraper.parsers.mime-message-parser :as parser])
  (:require appengine-clj.datastore))

(import '(com.google.appengine.tools.development.testing LocalServiceTestHelper LocalServiceTestConfig LocalDatastoreServiceTestConfig))
(import '(com.google.appengine.api.datastore DatastoreServiceFactory FetchOptions))
(import '(com.google.appengine.api.datastore Entity Query Text))
(import com.cyrusinnovation.mail_scraper.parsers.mime_message_parser.Message)

(background (around :facts (let [config (new LocalDatastoreServiceTestConfig)
                                 helper (new LocalServiceTestHelper (into-array LocalServiceTestConfig [config]))]
                             (.setUp helper)
                             ?form
                             (.tearDown helper))))

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

(fact "We can create a hash of storable fields from a Message record"
  (let [now (new java.util.Date)
        storable-hash (persister/storable-message-record (new Message
                                                            "Frank Denbow <a@b.com>"
                                                            now
                                                            "A subject"
                                                            "Text of a message"
                                                            "<p>HTML of a message</p>"
                                                            "The source of the message"))]
    (:from storable-hash) => "Frank Denbow <a@b.com>"
    (instance? java.util.Date (:sent-date storable-hash)) => true
    (:sent-date storable-hash) => now
    (:subject storable-hash) => "A subject"
    (instance? Text (:text storable-hash)) => true
    (-> (:text storable-hash) (.getValue)) => "Text of a message"
    (instance? Text (:html storable-hash)) => true
    (-> (:html storable-hash) (.getValue)) => "<p>HTML of a message</p>"
    (instance? Text (:source storable-hash)) => true
    (-> (:source storable-hash) (.getValue)) => "The source of the message"))
    
(fact "We can store the fields on a Message record to the app datastore"
  (let [now (new java.util.Date)
        message (new Message
                     "Frank Denbow <a@b.com>"
                     now
                     "A subject"
                     "Text of a message"
                     "<p>HTML of a message</p>"
                     "The source of the message")]
  (persister/store message)
  (let [result (appengine-clj.datastore/find-all (Query. "Message"))
        record (first result)]
    (count result) => 1
    (:from record) => "Frank Denbow <a@b.com>"
    (:sent-date record) => now
    (:subject record) => "A subject"
    (-> (:text record) (.getValue)) => "Text of a message"
    (-> (:html record) (.getValue)) => "<p>HTML of a message</p>"
    (-> (:source record) (.getValue)) => "The source of the message")))
    
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
    