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

set -e

usage() {
        echo "Usage: anuenue-commands COMMAND [--arg COMMAND_ARGUMENT] [--host HOST_NAME] [--port PORT_NUMBER]"
        echo "where COMMAND is one of:"
        echo "  commit               commit the changes of the index"
        echo "  softcommit           soft-commit the changes of the index"
        echo "  delete               delete index which given query mathes"
        echo "  deleteAll            delete all index"
        echo "  optimize             optimize indexes"
        echo "  post                 update contents of given file or directory to index via HTTP"
        echo "  updateViaStreamFile  update contents of given file to index via stream.file parameter of update/csv"
}



cd `dirname $0`/..

. bin/_function.sh

load_config_files

if [ $# -lt 1 ] ; then
        usage
        exit 1
fi

COMMAND=$1
shift

# get arguments
if ! OPTIONS=$(getopt -o a:h:p: -l arg:,host:,port: -- "$@")
then
        usage
        exit 1
fi

arg=""
host=""
port=""

while [ $# -gt 0 ]
do
        case $1 in
                -a|--arg) arg=$2 ; shift ;;
                -h|--host) host=$2 ; shift ;;
                -p|--port) port=$2 ; shift ;;
                --) break;;
        esac
        shift
done

COMMAND_OPTIONS=''
if [ x"$arg" != x"" ] ; then
        COMMAND_OPTIONS="$COMMAND_OPTIONS --arg '$arg'"
fi
if [ x"$host" != x"" ] ; then
        COMMAND_OPTIONS="$COMMAND_OPTIONS --host '$host'"
fi
if [ x"$port" != x"" ] ; then
        COMMAND_OPTIONS="$COMMAND_OPTIONS --port '$port'"
fi

# run COMMAND
if [ x"$COMMAND" = x"post" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg dir_or_file)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command addFile $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"updateViaStreamFile" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg file)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command updateViaStreamFile $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"commit" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command commit $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"softcommit" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command softcommit $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"delete" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg dir_or_file)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command delete $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"deleteAll" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command deleteAll $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"optimize" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueCommands --command optimize $COMMAND_OPTIONS
else
        error "Error: command $COMMAND is not found."
        usage
        exit
fi
