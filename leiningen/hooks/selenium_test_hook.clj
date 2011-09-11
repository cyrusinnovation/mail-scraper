(try
  (ns leiningen.hooks.selenium-test-hook
    (:use robert.hooke)
    (:require (leiningen test
                         [gae :as server]
                         [midje :as midje])))
  (catch java.io.FileNotFoundException e
    (.println *err* "NOTE: If this is the first time you are loading dependencies into this project, you will get a warning message with a FileNotFound exception when trying to load the Selenium test hook. This is a bootstrapping issue and is harmless. The hook should load and run properly once dependencies are loaded into your project.")
    (throw e)))

;; Running lein test will now start the server beforehand and stop it afterwards.
;; In addition it will use lein-midje to run the tests, to generate better reports.
(add-hook #'leiningen.test/test
          (fn [test project & args]
            (server/dev-appserver project)
            (midje/midje project)
            (server/kill-appserver)))

