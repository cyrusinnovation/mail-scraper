(ns end-to-end-tests
	(:use midje.sweet))

(import org.openqa.selenium.firefox.FirefoxDriver)
(import org.openqa.selenium.By)

(import org.apache.http.impl.client.DefaultHttpClient)
(import org.apache.http.client.methods.HttpPost)
(import '(org.apache.http HttpEntity NameValuePair HttpResponse))
(import org.apache.http.message.BasicNameValuePair)
(import org.apache.http.client.entity.UrlEncodedFormEntity)
(import org.apache.http.util.EntityUtils)

(defn http-execute [client method]
  (let [response (.execute client method)]
    (.getEntity response)))

(defn stringify-response [response-entity]
  (cond
   (nil? response-entity) ""
   (> 500000 (.getContentLength response-entity)) (throw (new RuntimeException "Content length too large."))
   :else (EntityUtils/toString response-entity)))
  
(defn post [url key-value-pairs]
  (let [client (new DefaultHttpClient)
        method (new HttpPost url)
        request-params (map (fn [pair] (new BasicNameValuePair (pair 0) (pair 1)))
                            (seq key-value-pairs))]
    (.setEntity method (new UrlEncodedFormEntity request-params "UTF-8"))
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
                      
(fact "Page with no events results from empty database"
			(.get driver "http://localhost:8080/report")
			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Networking Events"
			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "There are no networking events at this time.")

(fact "The application persists and retrieves an event"
  (let [timestamp (str (new java.util.Date))]
			(.get driver (str "http://localhost:8080/events?name=An%20Event%20with%20a%20%3Ctag%3E%20at%20" timestamp))
			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Submitted"
			(.get driver "http://localhost:8080/report")
			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => (str "An Event with a <tag> at " timestamp)))

(.quit driver))