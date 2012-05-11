#!/bin/bash

. functions

check_root
remove_deb libgeos-dev libgeos-c1 libgeos-3.3.3
install_deb geos libgeos-3.3.3 libgeos-c1 libgeos-dev
checkrc $? "installing geos libs"
