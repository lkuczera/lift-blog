java -XX:MaxPermSize=512m -Xmx512M -Xss2M -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/sbt-launcher.jar "$@"
