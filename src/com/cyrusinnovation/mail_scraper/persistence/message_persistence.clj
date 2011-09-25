(ns com.cyrusinnovation.mail-scraper.persistence.message-persistence
  (:require [appengine-clj.datastore :as ds]))

(import '(com.google.appengine.api.datastore Text))

(defn convert-if-long-text-field [key value]
  (if (some #{key} '(:text :html :source))  ; bizarre clojure way of indicating if item is in a list
    [key (new Text value)]
    [key value]))

(defn storable-message-record [parsed-message-record]
  (into {:kind "Message"}
        (for [[key val] parsed-message-record] (convert-if-long-text-field key val))))

(defn store [parsed-message-record]
  (let [map-to-store (storable-message-record parsed-message-record)]
    (ds/create map-to-store)))

