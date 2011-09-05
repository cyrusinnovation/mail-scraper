#!/bin/sh

if [ $# -ne 3 ] ; then
		echo "Usage: $0 APPSERVER_INSTALL_DIR GAE_HOST GAE_PORT"
		exit 1
fi

APPSERVER_SDK_INSTALL_PATH=$1
HOST=$2
PORT=$3
CLASSPATH=${APPSERVER_SDK_INSTALL_PATH}/lib/appengine-tools-api.jar
GAE_KICKSTART=com.google.appengine.tools.KickStart
GAE_MAIN=com.google.appengine.tools.development.DevAppServerMain

mkdir -p logs
nohup java -ea -cp $CLASSPATH $GAE_KICKSTART $GAE_MAIN --address=${HOST} --port=${PORT} war >> logs/gae.out 2>> logs/gae.err &
