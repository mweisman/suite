#!/usr/bin/env bash


echo "Building $1"

if [ "$1" == "stable" ]; then
  echo "deb http://apt.opengeo.org/ubuntu lucid main" >> /etc/apt/sources.list
else
  echo "deb http://apt.opengeo.org/$1 lucid main" >> /etc/apt/sources.list
fi


# Update apt
apt-get clean
apt-get update
apt-get upgrade -y

# Silently install the suite noninteractively
export DEBIAN_FRONTEND=noninteractive
apt-get install -y opengeo-suite gdal-mrsid