#!/bin/sh

# Starts, stops, and restarts Apache Solr.
#
# Requires setting JAVA_HOME and SOLR_HOME.
#

JAVA_OPTIONS="-Xms2048m -Xmx2048m -DSTOP.PORT=8079 -DSTOP.KEY=solr -jar start.jar"
LOG_FILE=$SOLR_HOME/logs/stdout.log
JAVA=$JAVA_HOME/bin/java

echo "SOLR_HOME=$SOLR_HOME"
echo "JAVA_HOME=$JAVA_HOME"

case $1 in
    start)
        echo "Starting Solr"
        cd $SOLR_HOME
        $JAVA $JAVA_OPTIONS > $LOG_FILE 2>&1 &
        ;;
    stop)
        echo "Stopping Solr"
        cd $SOLR_HOME
        $JAVA $JAVA_OPTIONS --stop
        ;;
    restart)
        $0 stop
        sleep 1
        $0 start
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}" >&2
        exit 1
        ;;
esac

