(ns endtoend.report-end-to-end-test
	(:use midje.sweet))

(import org.openqa.selenium.firefox.FirefoxDriver)
(import org.openqa.selenium.By)

;; Why isn't this working?
;; (background (around :contents (let [driver (new FirefoxDriver)]
;; 																?form
;; 																(.quit driver))))

(let [driver (new FirefoxDriver)]

(fact "Page with no events results from empty database"
			(.get driver "http://localhost:8080/report")
			(-> (.findElement driver (By/tagName "title")) (.getText)) => "Networking Events"
			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "There are no networking events at this time.")
	
;; (fact "The application persists an event"
;; 			(.get driver "http://localhost:8080/events?name=An%20Event%20with%20a%20%3Ctag%3E")
;; 			(.get driver "http://localhost:8080/report")
;; 			(-> (.findElement driver (By/cssSelector ".eventTitle")) (.getText)) => "An Event with a <tag>")

(.quit driver))