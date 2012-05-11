#!/bin/bash

. functions

build_info

PROJ=proj-4.8.0

# grab files
get_file http://download.osgeo.org/proj/$PROJ.tar.gz
get_file http://download.osgeo.org/proj/proj-datumgrid-1.5.zip

cp files/$PROJ.tar.gz $RPM_SOURCE_DIR
cp files/proj-datumgrid-1.5.zip $RPM_SOURCE_DIR

# build
build_rpm yes

# publish
publish_rpm

