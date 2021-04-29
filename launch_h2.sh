#!/usr/bin/env bash

#cd /opt/h2/bin
cd /opt/payara/appserver/h2db/bin
#java -cp "/opt/h2/bin/h2-1.4.197.jar:$H2DRIVERS:$CLASSPATH" org.h2.tools.Server "$@" &
java -cp "/opt/payara/appserver/h2db/bin/h2.jar:$H2DRIVERS:$CLASSPATH" org.h2.tools.Server "$@" &