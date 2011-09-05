#!/bin/sh

ps -opid,command | grep DevAppServerMain | grep -v grep | grep ' -ea ' | awk '{ print $1 }' | xargs kill
