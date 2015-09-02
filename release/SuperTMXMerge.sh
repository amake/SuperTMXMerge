#!/bin/sh

SCRIPT_DIR=$(dirname "$(readlink -f $0)")
java -jar "$SCRIPT_DIR/SuperTMXMerge.jar" "$@"
