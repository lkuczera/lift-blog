set MAVEN_OPTS=-XX:MaxPermSize=512m -Xmx512m
mvn jetty:run -D"jetty.reload=manual"