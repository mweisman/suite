#!/bin/bash

# find sdk home
if [ -z "$0" ]; then
    # as a last recourse, use the present working directory
    SDK_HOME=$(pwd)
else
    # save original working directory
    ORIG_PWD="$(pwd -P)"

    # get the absolute path of the executable
    SELF_PATH=$(
        cd -P -- "$(dirname -- "$0")" \
        && pwd -P
    ) && SELF_PATH=$SELF_PATH/$(basename -- "$0")

    # resolve symlinks
    while [ -h "$SELF_PATH" ]; do
        DIR=$(dirname -- "$SELF_PATH")
        SYM=$(readlink -- "$SELF_PATH")
        SELF_PATH=$(cd -- "$DIR" && cd -- $(dirname -- "$SYM") && pwd)/$(basename -- "$SYM")
    done

    SDK_HOME=$(dirname -- "$(dirname -- "$SELF_PATH")")

    # restore original working directory
    cd "$ORIG_PWD"

fi

# parse options
COMMAND=$1
OPTIND=2
ANT_ARGS=""
case $COMMAND in 
    debug )
        while getopts g:p: OPTION; do
            case $OPTION in
                g )
                    ANT_ARGS="$ANT_ARGS -Dapp.proxy.geoserver=$OPTARG" ;;
                p )
                    ANT_ARGS="$ANT_ARGS -Dapp.port=$OPTARG" ;;
            esac
        done ;;
    deploy )
        while getopts c:u:s:d:p: OPTION; do
            case $OPTION in
                c )
                    ANT_ARGS="$ANT_ARGS -Dsuite.container=$OPTARG" ;;
                u )
                    ANT_ARGS="$ANT_ARGS -Dsuite.username=$OPTARG" ;;
                s )
                    ANT_ARGS="$ANT_ARGS -Dsuite.password=$OPTARG" ;;
                d )
                    ANT_ARGS="$ANT_ARGS -Dsuite.domain=$OPTARG" ;;
                p )
                    ANT_ARGS="$ANT_ARGS -Dsuite.port=$OPTARG" ;;
            esac
        done ;;
esac
shift $(($OPTIND - 1))
APP_PATH=$1

ant -e -f $SDK_HOME/build.xml -Dsdk.home=$SDK_HOME -Dbasedir=. $COMMAND -Dapp.path=$APP_PATH $ANT_ARGS
