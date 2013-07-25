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

set -e

usage() {
        echo "Usage: anuenue-dist-commands COMMAND [--arg COMMAND_ARGMENT] [--conf CONF_FILE]"
        echo "where "
        echo "COMMAND: name of the command to run"
        echo "  delete        delete documents which are matched by specified query"
        echo "  commit        commit changes"
        echo "  softcommit    soft-commit changes"
        echo "  optimize      optimize indexes"
        echo "  post          post documents specified by a file or a directory"
        echo "  update        update documents specified by a file or a directory"
        echo "  batchdelete   delete documents specified by a file or a directory"
        echo ""
        echo "COMMAND_ARGUMENT: argument of each COMMAND"
        echo ""
        echo "CONF_FILE: node configuration file (anuenue-nodes.xml)."
        echo "          Users set CONF_FILE when the node configuration file is not in $ANUENUE_HOME."
}

cd `dirname $0`/..

. bin/_function.sh

load_config_files

if [ $# -lt 1 ] ; then
        usage
        exit 1
fi

# set paramters
COMMAND=$1
shift

if ! OPTIONS=$(getopt -o a:c: -l arg:,conf: -- "$@")
then
        usage
        exit 1
fi


arg=""
conf=""

while [ $# -gt 0 ]
do
        case $1 in
                -a|--arg) arg=$2 ; shift ;;
                -h|--conf) conf=$2 ; shift ;;
                --) break;;
        esac
        shift
done

COMMAND_OPTIONS=''
if [ x"$arg" != x"" ] ; then
        COMMAND_OPTIONS="$COMMAND_OPTIONS --arg '$arg'"
fi
if [ x"$conf" != x"" ] ; then
        COMMAND_OPTIONS="$COMMAND_OPTIONS --conf '$conf'"
fi

# run COMMAND
echo "[`date --rfc-3339=seconds`]," "executing anuenue-distcommands with the target command,$COMMAND"
if [ x"$COMMAND" = x"post" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg dir_or_file)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command addDir $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"commit" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command commit $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"softcommit" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command softcommit $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"delete" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg query)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command delete $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"batchDelete" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg dir_or_file)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command batchDelete $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"optimize" ] ; then
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command optimize $COMMAND_OPTIONS
elif [ x"$COMMAND" = x"update" ] ; then
        [ x"$arg" != x"" ] || error "Please specify arguments(--arg dir_or_file)"
        eval $ANUENUE_JAVA $COMMAND_JAVA_OPTS $ANUENUE_OPTS org.unigram.anuenue.client.AnuenueDistCommands --command updateDir $COMMAND_OPTIONS
else
        error "Error: nothing such a command like $COMMAND"
        usage
        exit
fi
