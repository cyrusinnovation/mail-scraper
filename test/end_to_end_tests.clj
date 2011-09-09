(ns end-to-end-tests
	(:use midje.sweet))

(import org.openqa.selenium.firefox.FirefoxDriver)
(import org.openqa.selenium.By)

;; Why isn't this working?
;; (background (around :contents (let [driver (new FirefoxDriver)]
;; 																?form
;; 																(.quit driver))))

(let [driver (new FirefoxDriver)]

;; Need to empty the database before these tests run
(fact "Page with no events results from empty database"
			(.get driver "http://localhost:8080/report")
			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Networking Events"
			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "There are no networking events at this time.")

;; Redo this using HTTP Post 
(fact "The application persists and retrieves an event"
  (let [timestamp (str (new java.util.Date))]
			(.get driver (str "http://localhost:8080/events?name=An%20Event%20with%20a%20%3Ctag%3E%20at%20" timestamp))
			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Submitted")
			(.get driver "http://localhost:8080/report")
			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => (str "An Event with a <tag> at " timestamp))

(.quit driver))