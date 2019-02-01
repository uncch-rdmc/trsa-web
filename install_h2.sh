#!/usr/bin/env bash
# quick and dirty for prototype

mkdir /opt/h2
curl -o /tmp/h2.zip http://www.h2database.com/h2-2018-03-18.zip
unzip -q /tmp/h2.zip -d /opt
cd /opt/h2/bin

# populate sample properties
echo "0=Generic H2 (Server)|org.h2.Driver|jdbc\:h2\:tcp\://localhost/~/trsa|impactUser" > ~/.h2.server.properties

# launch h2 in scripts/init.d/ instead
#java -cp "/opt/h2/bin/h2-1.4.197.jar:$H2DRIVERS:$CLASSPATH" org.h2.tools.Server "$@" &
