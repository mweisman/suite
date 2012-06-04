#!/usr/bin/env bash

set -e

if [[ -z $1 ]]; then
    echo "ERROR: Missing box name"
    echo "usage: run_box.sh <box> <repo>"
    exit 1
else
    BOX_NAME=$1
fi

if [[ -z $2 ]]; then
    echo "ERROR: Missing repo name"
    echo "usage: run_box.sh <box> <repo>"
    exit 1
else
    REPO=$2
fi

apply_file="class {'opengeosuite':\
  repo  => '${REPO}'\
}"

cd $BOX_NAME
echo $apply_file > manifests/apply.pp
echo "Building ${REPO} on ${BOX_NAME}"
vagrant up