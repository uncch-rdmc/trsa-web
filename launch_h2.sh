#!/usr/bin/env bash

cd /opt/h2/bin
java -cp "/opt/h2/bin/h2-1.4.197.jar:$H2DRIVERS:$CLASSPATH" org.h2.tools.Server "$@" &
