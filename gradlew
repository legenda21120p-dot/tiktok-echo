#!/usr/bin/env sh
APP_HOME=""
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -jar $CLASSPATH "$@"