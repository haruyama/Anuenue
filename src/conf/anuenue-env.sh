###########################################################
#
# Common environment variables for Anuenue cluster.
#
# NOTE: The settings in this file are overried with instance specific settings
# by anuenue-env-site.sh.
#
###########################################################

# Default Search Field (OPTIONAL, Default: text [solrconfig.xml])
# DEFAULT_SEARCH_FIELD=text

# Default Operator (Default: AND)
# DEFAULT_OPERATOR=AND

# Java CLASSPATH
# JAVA_HOME=/usr/lib/j2sdk1.6-sun

# Extra Java runtime options for anuenue servers. Empty by default (OPTIONAL)
# DAEMON_JAVA_OPTS=-server

# Extra CLASSPATH (OPTIONAL)
# ANUENUE_CLASSPATH=

# Stop key (default mixi)
# ANUENUE_STOP_KEY=mixi

# Stop port (default 8079)
# ANUENUE_STOP_PORT=8079

# Data dir (default $ANUENUE_HOME/sorl/collection1/data)
# DATA_DIR=./data

# SOLR_HOME directly (default $ANUENUE_HOME)
# SOLR_HOME=$ANUENUE_HOME

# anuenue log (default $ANUENUE_HOME/logs/anuenue_logs.txt)
# ANUENUE_DAEMON_LOG_FILE=$ANUENUE_HOME/logs/anuenue_logs.txt

############################################################
# Index setting
############################################################

# MERGE FACTOR
# MERGE_FACTOR=10

# Buffer size for indexer
# RAM_BUFFER_SIZE=32

# Maximum number of chcaracters for one field
# MAX_FIELD_LENGTH=1024

# Use compound file or not
# USE_COMPOUND_FILE=false

# RESULT WINDOW SIZE
# QUERY_RESULT_WINDOW_SIZE=40

# MAXIMUM NUMBER OF CACHED DOCUMENTS
# RESULT_MAXDOCS_CACHED=200

# FLAG: USE COLD SEARCHER OR NOT
# USE_COLD_SEARCHER=false

# MAXIMUM NUMBER OF WARMING SEARCHERS
# MAX_WARMING_SEARCHERS=2

############################################################
# Cache setting
############################################################

# FILTER_CACHE_SIZE=4096
# INIT_FILTER_CACHE_SIZE=4096
# FILTER_CACHE_AUTOWARM_COUNT=0

# QUERY_RESULT_CACHE_SIZE=4096
# INIT_QUERY_RESULT_CACHE_SIZE=4096
# QUERY_CACHE_AUTOWARM_COUNT=0

# DOCUMENT_CACHE_SIZE=4096
# INIT_DOCUMENT_CACHE_SIZE=4096
# DOCUMENT_CACHE_AUTOWARM_COUNT=0

# FIELD_VALUE_CACHE_SIZE=4096
# FIELD_VALUE_CACHE_AUTOWARM_COUNT=0
# FIELD_VALUE_CACHE_SHOW_ITEMS=32

############################################################
# Command setting
############################################################

# maximum lines to be sent at one time (default 100).
# MAXIMUM_LINE_ONE_TIME=1000

# factor of thread number which multiplies the size of master nodes (default 3.0).
# FACTOR_OF_THREAD_NUMBER=3.0

# HTTP connection timeout(msec, default 60000(1 min))
# HTTP_CONNECT_TIMEOUT=60000

# HTTP read timeout(msec, default 120000(2 min))
# HTTP_READ_TIMEOUT=120000

# HTTP read timeout for commit command (msec, default 600000(10 min))
# HTTP_READ_TIMEOUT_COMMIT=600000

# HTTP read timeout for optimize command(msec, default 3600000(60 min))
# HTTP_READ_TIMEOUT_OPTIMIZE=3600000

# HTTP read timeout for updateViaStreamFile command(msec, default 21600000(360 min))
# HTTP_READ_TIMEOUT_UPDATE_VIA_STREAM_FILE=21600000

# Extra Java runtime options for anuenue-commands. Empty by default (OPTIONAL).
# COMMAND_JAVA_OPTS=
