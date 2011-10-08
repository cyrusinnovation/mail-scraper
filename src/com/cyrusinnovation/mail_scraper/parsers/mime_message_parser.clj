(ns com.cyrusinnovation.mail-scraper.parsers.mime-message-parser
  (:require [clojure.contrib.string :as string]))

(import java.util.Properties)
(import javax.mail.Session)
(import '(javax.mail.internet InternetAddress MimeMessage))

(defrecord MessageRecord [from sent-date subject text html source preface])

(defn mime-message-from-stream [input-stream]
   (let [session (Session/getDefaultInstance (new Properties))]
     (new MimeMessage session input-stream)))

(defn from-address [mime-message] (-> (.getFrom mime-message) (InternetAddress/toString)))

(defn body-parts [mime-message]
  (let [mime-content (.getContent mime-message)
        part-count (.getCount mime-content)]
    (map (fn [index] (.getBodyPart mime-content index))
         (range part-count))))

(defn is-multipart? [mime-message]
  (instance? javax.mail.Multipart (.getContent mime-message)))

(defn body-parts-of-type [mime-message mime-type]
  (cond
   (.isMimeType mime-message mime-type) (list mime-message)
   (is-multipart? mime-message) (filter (fn [part] (.isMimeType part mime-type))
                                        (body-parts mime-message))
   :else nil))

(defn extract-text [parts]
  (apply str (map #(.getContent %) parts)))

(defn plain-text [mime-message]
  (let [text-parts (body-parts-of-type mime-message "text/plain")]
    (extract-text text-parts)))

(defn html-content [mime-message]
  (let [html-parts (body-parts-of-type mime-message "text/html")]
    (extract-text html-parts)))

(defn string-from-input-stream [input-stream-goddammit]
  (let [scanner (new java.util.Scanner input-stream-goddammit)]
    (.useDelimiter scanner "\\A")  ; beginning of input boundary character as tokenizer, to get the whole thing.
    (.next scanner)))

(defn message-source [mime-message]
  (let [headers (string/join "\n" (enumeration-seq (.getAllHeaderLines mime-message)))
        body (string-from-input-stream (.getInputStream mime-message))]
    (str headers body)))

(defn parse-mime-message [mime-message]
  (new MessageRecord
       (from-address mime-message)
       (.getSentDate mime-message)
       (.getSubject mime-message)
       (plain-text mime-message)
       (html-content mime-message)
       (message-source mime-message)
       nil))

(defn parse [input-stream]
  (-> (mime-message-from-stream input-stream) (parse-mime-message)))
