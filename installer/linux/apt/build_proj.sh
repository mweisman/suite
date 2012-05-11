#!/bin/bash

. functions

build_info

PROJ=proj-4.8.0

# grab files
get_file http://download.osgeo.org/proj/$PROJ.tar.gz
get_file http://download.osgeo.org/proj/proj-datumgrid-1.5.zip

# clean out old sources
pushd proj
ls | grep -v debian | xargs rm -rf
popd

# unpack sources
rm -rf $PROJ
tar xzvf files/$PROJ.tar.gz
mv $PROJ/* proj
checkrc $? "unpacking proj sources"
rmdir $PROJ

unzip files/proj-datumgrid-1.5.zip -d proj/nad 
checkrc $? "unpacking datum files"

# build
build_deb proj

# publish
publish_deb proj
