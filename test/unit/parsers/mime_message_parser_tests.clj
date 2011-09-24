(ns unit.parsers.mime-message-parser-tests
  (:use midje.sweet)
  (:require [com.cyrusinnovation.mail-scraper.parsers.mime-message-parser :as parser]))

(import java.io.FileInputStream)
(import java.util.Calendar)
(import java.util.Properties)
(import javax.mail.Session)
(import javax.mail.internet.MimeMessage)

(defn as-calendar [date]
  (let [calendar (new java.util.GregorianCalendar)]
    (.setTime calendar date)
    calendar))

(background (around :facts (let [file-input-stream (new FileInputStream "./test/data/startup-digest-mail.txt")
                                 mime-message (parser/mime-message-from-stream file-input-stream)]
                             ?form
                             )))

(fact "We can get a MIME message from an InputStream"
  (-> (.getContent mime-message) (.getBodyPart 0) (.getContentType)) => (has-prefix "text/plain"))

(fact "The record we create from a MIME message contains the From Address"
  (:from-address (parser/parse mime-message)) => "Frank Denbow <frank.denbow@thestartupdigest.com>")

(fact "The record we create from a MIME message contains the sent date"
  (let [date-as-calendar (-> (:sent-date (parser/parse mime-message)) (as-calendar))]
    (.get date-as-calendar Calendar/YEAR) => 2011
    (.get date-as-calendar Calendar/MONTH) => 8
    (.get date-as-calendar Calendar/DAY_OF_MONTH) => 6
    (.get date-as-calendar Calendar/HOUR_OF_DAY) => 8
    (.get date-as-calendar Calendar/MINUTE) => 3
    (.get date-as-calendar Calendar/SECOND) => 21))

(fact "The record we create from a MIME message contains the subject"
  (:subject (parser/parse mime-message)) => "NYC StartupDigest - September 6, 2011 | Mission50, Hack and Tell, Video Hackday")

(fact "We can extract body parts from a MIME message"
  (-> (parser/body-parts mime-message) (count)) => 2)

(fact "We can extract the plain text part of a MIME message"
  (-> (parser/body-parts-of-type mime-message "text/plain") (first) (.getContentType)) => (has-prefix "text/plain"))

(fact "We can extract the html part of a MIME message"
  (-> (parser/body-parts-of-type mime-message "text/html") (first) (.getContentType)) => (has-prefix "text/html"))

(fact "The record we create from a MIME message contains the plain text"
  (:text (parser/parse mime-message)) => (contains "Life is too short to work at a boring company"))

(fact "The record we create from a MIME message contains the HTML"
  (:html (parser/parse mime-message)) => (contains "<title>NYC StartupDigest - September 6, 2011 | Mission50, Hack and Tell"))

;; (fact "The record we create from a MIME message contains the original message"
;;   (:source (parser/parse mime-message)) => (contains "<title>NYC StartupDigest - September 6, 2011 | Mission50, Hack and Tell"))

(fact "We get a correct string for a message's text if the message is not multipart."
  (let [session (Session/getDefaultInstance (new Properties))
        message (new MimeMessage session)]
    (.setText message "foo bar bam")
    (:text (parser/parse message)) => "foo bar bam"))

(fact "We get an empty string for a message's HTML if a single-part MIME message has no HTML part."
  (let [session (Session/getDefaultInstance (new Properties))
        message (new MimeMessage session)]
    (.setText message "foo bar bam")
    (:html (parser/parse message)) => ""))

(fact "We get a correct string for a message's HTML if the message is not multipart."
  (let [session (Session/getDefaultInstance (new Properties))
        message (new MimeMessage session)]
    (.setContent message "<p>foo bar bam</p>" "text/html")
    (.saveChanges message)
    (:html (parser/parse message)) => "<p>foo bar bam</p>"))

(defn create-multipart-msg-without-text-part [mime-msg]
  (let [multipart (.getContent mime-msg)
        content-type (.getContentType mime-msg)
        session (Session/getDefaultInstance (new Properties))
        new-msg (new MimeMessage session)]
    (.removeBodyPart multipart (.getBodyPart multipart 0))
    (.setContent new-msg multipart content-type)
    (.saveChanges new-msg)
    new-msg))
    
(fact "We get an empty string for a message's text if a multipart MIME message has no text part."
  (let [message (create-multipart-msg-without-text-part mime-message)]
    (-> (.getContent message) (.getCount)) => 1
    (:text (parser/parse message)) => ""))
