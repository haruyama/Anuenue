#
# adding Java parameters
# 
if [ x"$JAVA_HOME" = x"" ]; then
  echo "Error: JAVA_HOME is not set."
  exit 1
fi

# CLASSPATH 
CLASSPATH="$ANUENUE_CLASSPATH:$ANUENUE_TOOLS_LIB_DIR/*:$ANUENUE_LIB_DIR/*"

ANUENUE_JAVA="$JAVA_HOME/bin/java -cp $CLASSPATH"
