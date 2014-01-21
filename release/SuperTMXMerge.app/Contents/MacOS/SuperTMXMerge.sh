#!/bin/sh

# Get rid of process serial number from GUI launch
if [[ "$*" == -psn* ]]; then
    shift
fi
java -Xdock:name=SuperTMXMerge -jar "$(dirname "$0")/SuperTMXMerge.jar" "$@"
