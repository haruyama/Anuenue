###########################################################
#
# Common environment variables for Anuenue cluster.
#
# NOTE: The settings in this file are overried with instance specific settings
# by anuenue-env-site.sh.
#
###########################################################

# Default Search Field (OPTIONAL, Default: text [solrconfig.xml])
DEFAULT_SEARCH_FIELD=subject

#Default Operator (Default: AND [solrconfig.xml])
DEFAULT_OPERATOR=AND

# Java CLASSPATH elements. Optional.
# JAVA_HOME=/usr/lib/j2sdk1.6-sun

# Extra Java runtime options for anuenue servers. Empty by default (OPTIONAL).
DAEMON_JAVA_OPTS=-server

# Extra CLASSPATH (OPTIONAL)
# ANUENUE_CLASSPATH=

# Top key (default mixi)
ANUENUE_STOP_KEY=kuke

# Stop port (default 8079)
ANUENUE_STOP_PORT=18000

# Data dir (default ANUENUE_HOME/data)
DATA_DIR=./data

# SOLR_HOME directly (default $ANUENUE_HOME)
# SOLR_HOME=$ANUENUE_HOME

# anuenue log (default $ANUENUE_HOME/logs/anuenue_logs.txt)
# ANUENUE_DAEMON_LOG_FILE=$ANUENUE_HOME/logs/anuenue_logs.txt

############################################################
# Index setting
############################################################

# MERGE FACTOR
MERGE_FACTOR=20

# Buffer size for indexer
RAM_BUFFER_SIZE=132

# Maximum number of chcaracters for one field
MAX_FIELD_LENGTH=10240

# Use compound file or not
USE_COMPOUND_FILE=true

# RESULT WINDOW SIZE
QUERY_RESULT_WINDOW_SIZE=400

# MAXIMUM NUMBER OF CACHED DOCUMENTS
RESULT_MAXDOCS_CACHED=2000

# FLAG: USE COLD SEARCHER OR NOT
USE_COLD_SEARCHER=true

# MAXIMUM NUMBER OF WARMING SEARCHERS
MAX_WARMING_SEARCHERS=20

############################################################
# Cache setting
############################################################

FILTER_CACHE_SIZE=512
INIT_FILTER_CACHE_SIZE=512
FILTER_CACHE_AUTOWARM_COUNT=0

QUERY_RESULT_CACHE_SIZE=512
INIT_QUERY_RESULT_CACHE_SIZE=512
QUERY_CACHE_AUTOWARM_COUNT=0

DOCUMENT_CACHE_SIZE=512
INIT_DOCUMENT_CACHE_SIZE=512
DOCUMENT_CACHE_AUTOWARM_COUNT=0

FIELD_VALUE_CACHE_SIZE=4096
FIELD_VALUE_CACHE_AUTOWARM_COUNT=0
FIELD_VALUE_CACHE_SHOW_ITEMS=32

############################################################
# Command setting
############################################################

# maximum lines to be sent at one time (default 100).
MAXIMUM_LINE_ONE_TIME=1000

# HTTP connection timeout(msec, default 60000(1 min))
HTTP_CONNECT_TIMEOUT=60000

# HTTP read timeout(msec, default 120000(2 min))
HTTP_READ_TIMEOUT=120000

# HTTP read timeout for commit command (msec, default 600000(10 min))
HTTP_READ_TIMEOUT_COMMIT=600000

# HTTP read timeout for optimize command(msec, default 3600000(60 min))
HTTP_READ_TIMEOUT_OPTIMIZE=3600000

# HTTP read timeout for optimize command(msec, default 21600000(360 min))
HTTP_READ_TIMEOUT_UPDATE_VIA_STREAM_FILE=3600000

# Extra Java runtime options for anuenue-commands. Empty by default (OPTIONAL).
COMMAND_JAVA_OPTS=-server
