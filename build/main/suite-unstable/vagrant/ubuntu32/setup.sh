#!/usr/bin/env bash

# Update apt
apt-get update

# Silently install the suite noninteractively
export DEBIAN_FRONTEND=noninteractive
apt-get install -y opengeo-suite gdal-mrsid