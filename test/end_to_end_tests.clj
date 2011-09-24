(ns end-to-end-tests
	(:use midje.sweet)
  (:require [clojure.string :as string]))

(import org.openqa.selenium.firefox.FirefoxDriver)
(import org.openqa.selenium.By)

(import org.apache.http.impl.client.DefaultHttpClient)
(import org.apache.http.client.methods.HttpPost)
(import '(org.apache.http HttpEntity NameValuePair HttpResponse))
(import org.apache.http.message.BasicNameValuePair)
(import org.apache.http.entity.StringEntity)
(import org.apache.http.client.entity.UrlEncodedFormEntity)
(import org.apache.http.util.EntityUtils)

(import java.net.URLEncoder)

;; Utility methods for doing HTTP Posts
(defn form-query-string [key-value-pairs]
  (string/join "&"
               (map (fn [pair] (str
                                (URLEncoder/encode (pair 0) "UTF-8")
                                "="
                                (URLEncoder/encode (pair 1) "UTF-8")))
                    (seq key-value-pairs))))

(defn form-url-encoded-body [key-value-pairs]
  (let [request-params (map (fn [pair] (new BasicNameValuePair (pair 0) (pair 1)))
                            (seq key-value-pairs))]
    (new UrlEncodedFormEntity request-params "UTF-8")))

(defn form-post-url [url-base key-value-pairs & body]
  (cond
   (empty? body) url-base
   (empty? key-value-pairs) url-base
   :else (str url-base "?"  (form-query-string key-value-pairs))))

(defn form-post-body [key-value-pairs body]
  (if (empty? body)
    (form-url-encoded-body key-value-pairs)
    (new StringEntity body "UTF-8")))

(defn http-execute [client method]
  (let [response (.execute client method)]
    (.getEntity response)))

(defn stringify-response [response-entity]
  (cond
   (nil? response-entity) ""
   (< 500000 (.getContentLength response-entity)) (throw (new RuntimeException "Content length too large."))
   :else (EntityUtils/toString response-entity)))
    
(defn post [url key-value-pairs & [body]]
  (let [client (new DefaultHttpClient)
        method (new HttpPost (form-post-url url key-value-pairs body))]
    (.setEntity method (form-post-body key-value-pairs body))
    (let [response (http-execute client method)]
      (try (stringify-response response)
           (finally (EntityUtils/consume response))))))


;; Why isn't this working?
;; (background (around :contents (let [driver (new FirefoxDriver)]
;; 																?form
;; 																(.quit driver))))

(let [driver (new FirefoxDriver)]

  (background (before :facts (try
                               (do
                                 (.get driver "http://localhost:8080/_ah/admin/datastore?kind=Event")
                                 (-> (.findElement driver (By/id "allkeys")) (.click))
                                 (-> (.findElement driver (By/id "delete_button")) (.click))
                                 (-> (.switchTo driver) (.alert) (.accept)))
                               (catch Throwable t :no-data-to-clear))))
                      
;; (fact "Page with no events results from empty database"
;; 			(.get driver "http://localhost:8080/report")
;; 			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Networking Events"
;; 			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "There are no networking events at this time.")

;; (fact "The application persists and retrieves an event"
;;   (let [timestamp (str (new java.util.Date))]
;; 			(.get driver (str "http://localhost:8080/events?name=An%20Event%20with%20a%20%3Ctag%3E%20at%20" timestamp))
;; 			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Submitted"
;; 			(.get driver "http://localhost:8080/report")
;; 			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => (str "An Event with a <tag> at " timestamp)))

;; (fact "The application can receive an event via HTTP post, and we can test HTTP posts"
;;   (.get driver "http://localhost:8080/report")
;;   (-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "There are no networking events at this time."
;;   (post "http://localhost:8080/events" {"name" "An event submitted via POST."}) => (contains "<title>Submitted</title>")
;;   (.get driver "http://localhost:8080/report")
;;   (-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "An event submitted via POST.")

(fact "We can post a mail message to the application."
  (let [mail-message (slurp "./test/data/startup-digest-mail.txt")]
  (post "http://localhost:8080/events" {"name" "An event submitted via POST."} mail-message) => (contains "<title>Submitted</title>")
  (.get driver "http://localhost:8080/report")
  (-> (.findElement driver (By/cssSelector ".message")) (.getText)) => "Frank Denbow"))
  

  (.quit driver))


;;; Facts to test utility methods

;; (fact
;;   (form-query-string {}) => "")

;; (fact
;;   (form-query-string { "name" "value" }) => "name=value")

;; (fact
;;   (form-query-string { "name1" "value1" "name2" "value2" }) => "name1=value1&name2=value2")

;; (fact
;;   (form-query-string { "name1" "value1" "name 2" "value /2" }) => "name1=value1&name+2=value+%2F2")

;; (fact
;;   (form-post-url "http://localhost" {}) => "http://localhost")

;; (fact
;;   (form-post-url "http://localhost" {} :somebody) => "http://localhost")

;; (fact
;;   (form-post-url "http://localhost" { "name1" "value1" "name2" "value2" } :somebody) => "http://localhost?name1=value1&name2=value2")
