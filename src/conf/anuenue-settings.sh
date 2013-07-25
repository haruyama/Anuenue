############################################################
#
# Load parameters based on users settings
#
############################################################

# load variables on Java
if [ x"$ANUENUE_JAVA" = x"" ]; then
    . "${ANUENUE_HOME}/conf/anuenue-settings-java.sh"
fi

############################################################
# basic settings
############################################################

ANUENUE_SLAVE_SLEEP=1

SOLR_HOME=${SOLR_HOME:-$ANUENUE_HOME/solr}

# check envvars which might override default args

ANUENUE_STOP_KEY=${ANUENUE_STOP_KEY:-mixi}
ANUENUE_STOP_PORT=${ANUENUE_STOP_PORT:-8079}
ANUENUE_DAEMON_LOG_FILE=${ANUENUE_DAEMON_LOG_FILE:-$ANUENUE_HOME/logs/anuenue_logs.txt}

# add common options
ANUENUE_OPTS="-Dsolr.solr.home=$SOLR_HOME"
ANUENUE_OPTS="$ANUENUE_OPTS -DSTOP.PORT=$ANUENUE_STOP_PORT"
ANUENUE_OPTS="$ANUENUE_OPTS -DSTOP.KEY=$ANUENUE_STOP_KEY"

# add data dir
if [ x"$DATA_DIR" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.data.dir=$DATA_DIR"
fi


############################################################
# add index setting
############################################################

if [ x"$MERGE_FACTOR" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.merge.factor=$MERGE_FACTOR"
fi

if [ x"$RAM_BUFFER_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.rambuffer.size=$RAM_BUFFER_SIZE"
elif [ x"$LUCENE_BUFFER_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.rambuffer.size=$LUCENE_BUFFER_SIZE"
fi

if [ x"$MAX_FIELD_LENGTH" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.max.field.length=$MAX_FIELD_LENGTH"
fi

if [ x"$USE_COMPOUND_FILE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.useCompoundFile=$USE_COMPOUND_FILE"
fi

if [ x"$QUERY_RESULT_WINDOW_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.query.result.windowsize=$QUERY_RESULT_WINDOW_SIZE"
fi

if [ x"$RESULT_MAXDOCS_CACHED" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.query.result.maxdocs.cached=$RESULT_MAXDOCS_CACHED"
fi

if [ x"$USE_COLD_SEARCHER" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.use.coldsearcher=$USE_COLD_SEARCHER"
fi

if [ x"$MAX_WARMING_SEARCHERS" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.max.warming.searchers=$MAX_WARMING_SEARCHERS"
fi

if [ x"$DEFAULT_SEARCH_FIELD" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.default.search.field=$DEFAULT_SEARCH_FIELD"
fi

if [ x"$DEFAULT_OPERATOR" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.default.operator=$DEFAULT_OPERATOR"
fi

############################################################
# add cache setting
############################################################

# filterCache
if [ x"$FILTER_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.filter.cache.size=$FILTER_CACHE_SIZE"
fi

if [ x"$INIT_FILTER_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.init.filter.cache.size=$INIT_FILTER_CACHE_SIZE"
fi

if [ x"$FILTER_CACHE_AUTOWARM_COUNT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.filter.cache.autowarm.count=$FILTER_CACHE_AUTOWARM_COUNT"
fi

# queryResultCache
if [ x"$QUERY_RESULT_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.query.cache.size=$QUERY_RESULT_CACHE_SIZE"
fi

if [ x"$INIT_QUERY_RESULT_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.init.query.cache.size=$INIT_QUERY_RESULT_CACHE_SIZE"
fi

if [ x"$QUERY_CACHE_AUTOWARM_COUNT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.query.cache.autowarm.count=$QUERY_CACHE_AUTOWARM_COUNT"
fi

# documentCache
if [ x"$DOCUMENT_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.document.cache.size=$DOCUMENT_CACHE_SIZE"
fi

if [ x"$INIT_DOCUMENT_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.init.document.cache.size=$INIT_DOCUMENT_CACHE_SIZE"
fi

if [ x"$DOCUMENT_CACHE_AUTOWARM_COUNT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.document.cache.autowarm.count=$DOCUMENT_CACHE_AUTOWARM_COUNT"
fi

# fieldValueCache
if [ x"$FIELD_VALUE_CACHE_SIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.field.cache.size=$FIELD_VALUE_CACHE_SIZE"
fi

if [ x"$FIELD_VALUE_CACHE_AUTOWARM_COUNT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.field.cache.autowarm.count=$FIELD_VALUE_CACHE_AUTOWARM_COUNT"
fi

if [ x"$FIELD_VALUE_CACHE_SHOW_ITEMS" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Dsolr.field.cache.show.items=$FIELD_VALUE_CACHE_SHOW_ITEMS"
fi

############################################################
# add command specific options
############################################################

if [ x"$MAXIMUM_LINE_ONE_TIME" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.post.max.line=$MAXIMUM_LINE_ONE_TIME"
fi

if [ x"$FACTOR_OF_THREAD_NUMBER" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.post.factor.of.thread.number=$FACTOR_OF_THREAD_NUMBER"
fi

if [ x"$HTTP_CONNECT_TIMEOUT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.http.connet.timeout=$HTTP_CONNECT_TIMEOUT"
fi

if [ x"$HTTP_READ_TIMEOUT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.http.read.timeout=$HTTP_READ_TIMEOUT"
fi

if [ x"$HTTP_READ_TIMEOUT_COMMIT" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.http.read.timeout.commit=$HTTP_READ_TIMEOUT_COMMIT"
fi

if [ x"$HTTP_READ_TIMEOUT_OPTIMIZE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.http.read.timeout.optimize=$HTTP_READ_TIMEOUT_OPTIMIZE"
fi

if [ x"$HTTP_READ_TIMEOUT_UPDATE_VIA_STREAM_FILE" != x"" ]; then
    ANUENUE_OPTS="$ANUENUE_OPTS -Danuenue.http.read.timeout.updateviastreamfile=$HTTP_READ_TIMEOUT_UPDATE_VIA_STREAM_FILE"
fi
