(ns unit.parsers.mime-message-parser-tests
  (:use midje.sweet)
  (:require [com.cyrusinnovation.mail-scraper.parsers.mime-message-parser :as parser])
  (:require [clojure.contrib.string :as string]))

(import java.io.FileInputStream)
(import java.util.Calendar)
(import java.util.Properties)
(import javax.mail.Session)
(import javax.mail.internet.MimeMessage)

(defn as-calendar [date]
  (let [calendar (new java.util.GregorianCalendar)]
    (.setTime calendar date)
    calendar))

(background (around :facts (let [file-input-stream (new FileInputStream "./test/data/startup-digest-mail.txt")]
                             ?form
                             )))

(fact "We can get a MIME message from an InputStream"
  (let [mime-message (parser/mime-message-from-stream file-input-stream)]
    (-> (.getContent mime-message) (.getBodyPart 0) (.getContentType)) => (has-prefix "text/plain")))

(fact "We can extract body parts from a MIME message"
  (let [mime-message (parser/mime-message-from-stream file-input-stream)]
    (-> (parser/body-parts mime-message) (count)) => 2))

(fact "We can extract the plain text part of a MIME message"
  (let [mime-message (parser/mime-message-from-stream file-input-stream)]
    (-> (parser/body-parts-of-type mime-message "text/plain") (first) (.getContentType)) => (has-prefix "text/plain")))

(fact "We can extract the html part of a MIME message"
  (let [mime-message (parser/mime-message-from-stream file-input-stream)]
    (-> (parser/body-parts-of-type mime-message "text/html") (first) (.getContentType)) => (has-prefix "text/html")))

(fact "The record we create from an input stream contains the From Address"
  (:from (parser/parse file-input-stream)) => "Frank Denbow <frank.denbow@thestartupdigest.com>")

(fact "The record we create from an input stream contains the sent date"
  (let [date-as-calendar (-> (:sent-date (parser/parse file-input-stream)) (as-calendar))]
    (.get date-as-calendar Calendar/YEAR) => 2011
    (.get date-as-calendar Calendar/MONTH) => 8
    (.get date-as-calendar Calendar/DAY_OF_MONTH) => 6
    (.get date-as-calendar Calendar/HOUR_OF_DAY) => 8
    (.get date-as-calendar Calendar/MINUTE) => 3
    (.get date-as-calendar Calendar/SECOND) => 21))

(fact "The record we create from an input stream contains the subject"
  (:subject (parser/parse file-input-stream)) => "NYC StartupDigest - September 6, 2011 | Mission50, Hack and Tell, Video Hackday")

(fact "The record we create from an input stream contains the plain text"
    (:text (parser/parse file-input-stream)) => (contains "Privately see your options to")) ; this will fail if we don't correctly process MIME linebreaks

(fact "The record we create from an input stream contains the HTML"
  (:html  (parser/parse file-input-stream)) 
  => (contains "<title>NYC StartupDigest - September 6, 2011 | Mission50, Hack and Tell, Video"))  ; this will fail if we don't correctly process MIME linebreaks

(fact "The record we create from an input stream contains the original mail message"
  (let [source (string/split-lines (:source (parser/parse file-input-stream)))]
    (first source) => "Delivered-To: nul@bitbucket.net"
    (second source) => "Received: by 10.150.95.9 with SMTP id s9cs132021ybb;"
    (last source) => "--_----------=_MCPart_352594967--"))

(fact "We get a correct string for a message's text if the message is not multipart."
  (let [session (Session/getDefaultInstance (new Properties))
        message (new MimeMessage session)]
    (.setText message "foo bar bam")
    (:text (parser/parse-mime-message message)) => "foo bar bam"))

(fact "We get an empty string for a message's HTML if a single-part MIME message has no HTML part."
  (let [session (Session/getDefaultInstance (new Properties))
        message (new MimeMessage session)]
    (.setText message "foo bar bam")
    (:html (parser/parse-mime-message message)) => ""))

(fact "We get a correct string for a message's HTML if the message is not multipart."
  (let [session (Session/getDefaultInstance (new Properties))
        message (new MimeMessage session)]
    (.setContent message "<p>foo bar bam</p>" "text/html")
    (.saveChanges message)
    (:html (parser/parse-mime-message message)) => "<p>foo bar bam</p>"))

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
  (let [mime-message (parser/mime-message-from-stream file-input-stream)
        message (create-multipart-msg-without-text-part mime-message)]
    (-> (.getContent message) (.getCount)) => 1
    (:text (parser/parse-mime-message message)) => ""))
