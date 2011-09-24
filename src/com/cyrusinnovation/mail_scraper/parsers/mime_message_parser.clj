(ns com.cyrusinnovation.mail-scraper.parsers.mime-message-parser)
(import java.util.Properties)
(import javax.mail.Session)
(import '(javax.mail.internet InternetAddress MimeMessage))

(defrecord Message [from-address sent-date subject text html])

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
  
(defn parse [mime-message]
  (Message. (from-address mime-message)
            (.getSentDate mime-message)
            (.getSubject mime-message)
            (plain-text mime-message)
            (html-content mime-message)))