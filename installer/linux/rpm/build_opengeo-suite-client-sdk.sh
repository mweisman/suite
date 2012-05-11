#!/bin/bash

. functions

build_info

# grab files
SDK_DIR=opengeosuite-$REV-sdk.zip
get_file $BUILDS/$DIST_PATH/$REV/$SDK_DIR yes

# clean out old files
clean_src

# copy over files
mkdir ${PKG_SOURCE_DIR}
unzip files/$SDK_DIR -d ${PKG_SOURCE_DIR}/..
mv ${PKG_SOURCE_DIR}/../opengeosuite-*-sdk/* ${PKG_SOURCE_DIR}/.
checkrc $? "unpacking sdk directory"

# build
build_rpm

# publish
publish_rpm

