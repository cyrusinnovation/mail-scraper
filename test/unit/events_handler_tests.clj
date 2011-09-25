(ns unit.events-handler-tests
  (:use midje.sweet)
  (:require [com.cyrusinnovation.mail-scraper.handlers.events-handler :as handler])
  (:require [com.cyrusinnovation.mail-scraper.parsers.mime-message-parser :as parser])
  (:require [com.cyrusinnovation.mail-scraper.persistence.message-persistence :as persister]))

(fact "event handler collaborates properly to store an incoming message"
  (handler/save-incoming-message ...message...) => true
  (provided
    (parser/parse ...message...) => ...message-record...
    (persister/store ...message-record...) => true))