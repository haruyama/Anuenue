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

cd `dirname $0`/..

. bin/_function.sh

load_config_files

# default config
TARGET_DIR=$ANUENUE_HOME
SSH_USER=$USER
SUDO_DAEMON=""
CONF_DIR=$ANUENUE_HOME/conf
CONF_SYNC=yes

# if no args specified, show usage
if [ $# = 0 ]; then
    echo "Usage: anuenue COMMAND [-c CONF_DIR] [-d TARGET_DIR] [-u DAEMON USER] [-l SSH USER] [-s]"
    echo "where COMMAND is one of:"
    echo "  start    start anuenue cluster"
    echo "  stop     stop anuenue cluster"
    echo "CONF_DIR is the directory which has configuration files for the cluster"
    echo "TARGET_DIR is the directory which is ANUENUE_HOME of the computers in Solr cluster"
    exit 1
fi

# load parameters for cluster information
COMMAND=$1
shift

while getopts "c:d:u:l:s" OPTION; do
    case "$OPTION" in
	c)
 	  CONF_DIR="$OPTARG"
	  ;;
	d)
	  TARGET_DIR="$OPTARG"
	  ;;
	u)
	  SUDO_DAEMON="sudo -H -u $OPTARG"
	  ;;
	l)
   	  SSH_USER="$OPTARG"
          ;;
	s)
	  CONF_SYNC=no	
	  ;;
    esac
done


# CLASSPATH
CLASSPATH=$CLASSPATH:$ANUENUE_LIB_DIR:$ANUENUE_CLASSPATH:$ANUENUE_HOME/anuenue-lib/*
HOST_LIST=`$ANUENUE_JAVA org.unigram.anuenue.client.GetNodesInCluster --conf $CONF_DIR/anuenue-nodes.xml`

# startup all instances
for node_info in $HOST_LIST; do
    node=`expr $node_info : '^\(.*\)_.*_.*$'`
    port=`expr $node_info : '^.*_\(.*\)_.*$'`
    if [ x"$CONF_SYNC" != x"no" ]; then
    	    scp -q $CONF_DIR/anuenue-env.sh "$SSH_USER@$node:$TARGET_DIR/conf" || error "scp anuenue-env.sh error"
    	    scp -q $CONF_DIR/anuenue-nodes.xml "$SSH_USER@$node:$TARGET_DIR/conf" || error "scp anuenue-nodes.xml error"
    fi
    if [ x"$SUDO_DAEMON" != x"" ]; then
            ssh -t $SSH_USER@$node "$SUDO_DAEMON sh -c 'touch $ANUENUE_DAEMON_LOG_FILE && nohup sh $TARGET_DIR/bin/anuenue-daemon.sh $COMMAND $node $port >> $ANUENUE_DAEMON_LOG_FILE 2>&1' " || error "Anuenue remote execution failed: $node:$port $COMMAND [NG]"
    else
            ssh $SSH_USER@$node -n "touch $ANUENUE_DAEMON_LOG_FILE && sh $TARGET_DIR/bin/anuenue-daemon.sh $COMMAND $node $port >> $ANUENUE_DAEMON_LOG_FILE" 2>&1 || error "Anuenue remote execution failed: $node:$port $COMMAND [NG]"
    fi
    echo "$node:$port $COMMAND [OK]"
    if [ x"$ANUENUE_NODE_SLEEP" != x"" ]; then
        sleep $ANUENUE_SLAVE_SLEEP
    fi
done
