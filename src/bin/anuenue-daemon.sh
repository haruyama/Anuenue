#!/usr/bin/env bash

# Copyright (c) The Anuenue Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# workaround for garbled characters of log.
[ x"" != x"${LANG}" ] || LANG='en_US.UTF-8'

cd `dirname $0`/..

. bin/_function.sh

load_config_files

# if no args specified, show usage
if [ $# = 0 ]; then
    echo "Usage: anuenue COMMAND [hostname] [port-number]"
    echo "where COMMAND is one of:"
    echo "  start      start anuenue"
    echo "  stop       stop anuenue"
    exit 1
fi

# get host name and base port if they are given.
if [ $# != 0 ] ; then
    COMMAND=$1
    shift
fi
if [ $# != 0 ] ; then
    ANUENUE_HOST_NAME=$1
    shift
else
    ANUENUE_HOST_NAME=`hostname`
fi
if [ $# != 0 ] ; then
    ANUENUE_BASE_PORT=$1
    shift
else
    ANUENUE_BASE_PORT=8983
fi

# generate a setting file
if [ "$COMMAND" = "start" ]; then
    eval `$ANUENUE_JAVA org.unigram.anuenue.client.GetInstanceProperties --conf $ANUENUE_HOME/conf/anuenue-nodes.xml --port $ANUENUE_BASE_PORT --host $ANUENUE_HOST_NAME`
    if [ "$?" -ne 0 ]; then
        error "Invalid parameters. Stopping to start daemon..."
    fi
fi

# read the generated setting file
#. "$ANUENUE_HOME/conf/anuenue-env-gen.sh"

# add instance information

if [ "$ANUENUE_HOST_NAME" != "" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.hostName=$ANUENUE_HOST_NAME"
fi

if [ "$ANUENUE_BASE_PORT" != "" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.basePort=$ANUENUE_BASE_PORT"
    ANUENUE_OPTS="$ANUENUE_OPTS -Djetty.port=$ANUENUE_BASE_PORT"
fi

# add replication options
if [ "$INSTANCE_TYPE" = "INDEX" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Denable.master=true"
fi

if [ "$INSTANCE_TYPE" = "REPLICATE" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Denable.slave=true"
fi

if [ "$REPLICATION_MASTER_NODE" != "" -a "$REPLICATION_MASTER_PORT" != "" ]; then
    REPLICATION_MASTER_URL="http://$REPLICATION_MASTER_NODE:$REPLICATION_MASTER_PORT/solr/replication"
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.replication.master.url=$REPLICATION_MASTER_URL"
fi

# run COMMAND
if [ "$COMMAND"  = "start" ] ; then
    echo a | nc localhost $ANUENUE_STOP_PORT 2> /dev/null
    if [ $? -ne 1 ] ; then
        error "Error: stop port already in use?: $ANUENUE_STOP_PORT"
    fi
    curl -s http://$ANUENUE_HOST_NAME:$ANUENUE_BASE_PORT/
    if [ $? -ne 7 ] ; then
        error "Error: base port already in use?: $ANUENUE_BASE_PORT"
    fi
    ANUENUE_START_OPTS="$ANUENUE_HOME/conf/jetty.xml"
    exec $ANUENUE_JAVA $DAEMON_JAVA_OPTS $ANUENUE_OPTS -jar $ANUENUE_HOME/lib/start.jar $ANUENUE_START_OPTS &
    if [ $? -ne 0 ] ; then
        error "Error: anuenue cannot start"
    fi
elif [ "$COMMAND" = "stop" ] ; then
    exec $ANUENUE_JAVA $DAEMON_JAVA_OPTS $ANUENUE_OPTS -jar $ANUENUE_HOME/lib/start.jar --stop &
    if [ $? -ne 0 ] ; then
        error "Error: anuenue cannot stop"
    fi
else
    error "Error: nothing such a command like $COMMAND"
fi
