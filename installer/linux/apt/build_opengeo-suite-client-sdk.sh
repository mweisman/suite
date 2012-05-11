#!/bin/bash

. functions

build_info

# grab files
SDK_DIR=opengeosuite-$REV-sdk.zip
get_file $BUILDS/$DIST_PATH/$REV/$SDK_DIR yes

# clean out old files
rm -rf opengeo-suite-client-sdk/opengeo-suite-client-sdk
unzip files/$SDK_DIR -d opengeo-suite-client-sdk
mv opengeo-suite-client-sdk/opengeosuite-*-sdk opengeo-suite-client-sdk/opengeo-suite-client-sdk

checkrc $? "unpacking sdk"

# build
build_deb opengeo-suite-client-sdk

# publish
publish_deb opengeo-suite-client-sdk
