error() {
    echo $1 1>&2
    exit 1
}

load_config_files() {
    if [ -f "bin/anuenue-config.sh" ]; then
        . "bin/anuenue-config.sh"
    fi

    # read the common settings in the cluster
    if [ -f "${ANUENUE_HOME}/conf/anuenue-env.sh" ]; then
        . "${ANUENUE_HOME}/conf/anuenue-env.sh"
    fi 

    # read the instance specific settings
    if [ -f "${ANUENUE_HOME}/conf/anuenue-env-site.sh" ]; then
        . "${ANUENUE_HOME}/conf/anuenue-env-site.sh"
    fi 

        # load optinal settings
    if [ -f "${ANUENUE_HOME}/conf/anuenue-settings.sh" ]; then
        . "${ANUENUE_HOME}/conf/anuenue-settings.sh"
    fi
}
