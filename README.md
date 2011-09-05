Beget
=====

Scrape incoming emails and store them in a database, then
present the relevant fields on a web page upon request.

Google App Engine project with Clojure and Leiningen.

Usage
=====

        $ lein deps

        $ lein compile

        $ lein gae start # Starts the development appserver

        $ lein gae stop  # Stops the development appserver

        $ lein uat       # Starts the dev server, runs end-to-end tests, and stops the server.
							 					 # For this you need to add the Firefox binary to your PATH
												 # E.g., >	export PATH=$PATH:/Applications/Firefox.app/Contents/MacOS

