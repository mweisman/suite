#!/usr/bin/env bash

echo "Building $1"

cd /etc/yum.repos.d/
if [ "$1" == "stable" ]; then
  sudo curl -O http://yum.opengeo.org/centos/5/x86_64/OpenGeo.repo
else
  sudo curl -O http://yum.opengeo.org/$1/centos/5/x86_64/OpenGeo.repo
fi

# Update yum
yum clean all
yum update -y

# Silently install the suite noninteractively
yum -y install opengeo-suite gdal-mrsid
