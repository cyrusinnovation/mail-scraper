Mail-scraper
=====

Scrape incoming emails and store them in a database, then
present the relevant fields on a web page upon request.

Google App Engine project with Clojure and Leiningen.

Usage
=====

The following requires [Leiningen](https://github.com/technomancy/leiningen) to be 
installed as well as the [Google App Engine SDK](http://code.google.com/appengine/downloads.html). 
You will need to edit the project.clj file to indicate the version
number of the App Engine SDK as well as the path where it is installed.

After that, you can run:

        $ lein deps

        $ lein compile

        $ lein gae start # Starts the development appserver

        $ lein gae stop  # Stops the development appserver

        $ lein test 		 # Starts the dev server, runs the end-to-end Selenium tests, and stops the server.
						 # For this you also need to add the Firefox binary to your PATH
						 # E.g.,  $>	export PATH=$PATH:/Applications/Firefox.app/Contents/MacOS

The shell scripts that start and stop the server currently presuppose some flavor
of UNIX, and have only been tested on OS X.

Use "lein test" only for running the entire test suite. To run single unit tests from Leiningen, use

        $ lein midje ns.qualified.test.name

Development
=====

The tests use Brian Marick's midje testing framework; midje is designed to work particularly
well with emacs. If you are using emacs, you will probably already have installed slime, 
slime-edit, and paredit through the ELPA package manager. However, ELPA may not have the 
latest [clojure-mode](https://github.com/technomancy/clojure-mode); I suggest getting at 
least version 1.10.0 in order to be able to work with swank-clojure. Next, install the
the [swank-clojure](https://github.com/technomancy/swank-clojure) Leiningen plugin, and
finally, get [midje-mode] (https://github.com/marick/Midje/wiki/Midje-mode). 
