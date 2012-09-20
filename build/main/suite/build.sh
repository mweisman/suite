#!/bin/bash

# load common functions
. "$( cd "$( dirname "$0" )" && pwd )"/functions 

#
# function to rebuild with a specific profile
# profile_rebuild <profile>
#
function profile_rebuild {
  local profile=$1

  pushd geoserver/web/app
  $MVN -s $MVN_SETTINGS -o clean install -P $profile -Dbuild.revision=$revision -Dbuild.date=$BUILD_ID
  checkrv $? "maven clean install geoserver/web/app ($profile profile)"
  popd

  pushd dashboard
  $MVN -s $MVN_SETTINGS -o clean install -P $profile -Dbuild.revision=$revision -Dbuild.date=$BUILD_ID
  checkrv $? "maven clean install dashboard ($profile profile)"
  popd

  $MVN -s $MVN_SETTINGS -P $profile -o assembly:attached -Dbuild.revision=$revision -Dbuild.date=$BUILD_ID -Dbuild.profile=$profile
  checkrv $? "maven assembly ($profile profile)"
}

set -x

DIST_PATH=`init_dist_path $DIST_PATH`

ALIAS=$REV
if [ "$ALIAS" == "HEAD" ]; then
  ALIAS="latest"
fi

# set up the maven repository for this particular branch/tag/etc...
#TODO: fix dist_path logic and how it relates to maven repo, etc...
MVN_SETTINGS=`init_mvn_repo latest`
export MAVEN_OPTS=-Xmx256m

# checkout the requested revision to build
cd repo
if [ ! -z $REV ]; then
  git checkout $REV

  # if this rev is a tag don't pull
  if [ "$( git tag | grep $REV )" != $REV ]; then
     git pull origin $REV
  fi

  git submodule update --init --recursive
fi

# extract the revision number
export revision=`get_rev .`

gs_externals="geoserver/externals"
gs_rev=`get_submodule_rev $gs_externals/geoserver`
gs_branch=`get_submodule_branch $gs_externals/geoserver`
gt_rev=`get_submodule_rev $gs_externals/geotools`
gt_branch=`get_submodule_branch $gs_externals/geotools`
gwc_rev=`get_submodule_rev $gs_externals/geowebcache`
gwc_branch=`get_submodule_branch $gs_externals/geowebcache`

# only use first seven chars
revision=${revision:0:7}

echo "building $revision ($REV) with maven settings $MVN_SETTINGS"

dist=/var/www/suite/$DIST_PATH/$revision
if [ ! -e $dist ]; then
  mkdir -p $dist
fi

echo "exporting artifacts to: $dist"

# perform a full build
$MVN -s $MVN_SETTINGS -Dfull -Dmvn.exec=$MVN -Dmvn.settings=$MVN_SETTINGS -Dbuild.revision=$revision -Dbuild.date=$BUILD_ID -Dgs.flags="-Dbuild.commit.id=$gs_rev -Dbuild.branch=$gs_branch -DGit-Revision=$gt_rev -Dgt.Git-Revision=$gt_rev" $BUILD_FLAGS clean install
checkrv $? "maven install"

$MVN -o -s $MVN_SETTINGS assembly:attached -Dbuild.revision=$revision
checkrv $? "maven assembly"

$MVN -s $MVN_SETTINGS -Dmvn.exec=$MVN -Dmvn.settings=$MVN_SETTINGS deploy -DskipTests
checkrv $? "maven deploy"

# build with the enterprise profile
profile_rebuild ee

# copy the new artifacts into place
cp target/*.zip target/ee/*.zip $dist

# Archive build if requested
if [ "$ARCHIVE_BUILD" == "true" ]; then
  cp -r $dist /var/www/suite/archive/$ALIAS
else
  ARCHIVE_BUILD="false"
fi

# clean up old artifacts
pushd $dist/..
# keep around last two builds
ls -lt | grep -v "^l" | cut -d ' ' -f 8 | tail -n +3 | xargs rm -rf 
popd

# start_remote_job <url> <name> <profile>
function start_remote_job() {
   curl -k --connect-timeout 10 "$1/buildWithParameters?DIST_PATH=${DIST_PATH}&REVISION=${revision}&ALIAS=${ALIAS}&PROFILE=$3&ARCHIVE_BUILD=${ARCHIVE_BUILD}"
   checkrv $? "trigger $2 $3 with ${DIST_PATH} r${revision}"
}

WIN=192.168.50.40
OSX=192.168.50.35

# start the build of the OSX installer
start_remote_job http://$OSX:8080/job/osx-installer "osx installer"

# start the build of the OSX installer (ee)
start_remote_job http://$OSX:8080/job/osx-installer "osx installer" ee

# start the build of the Windows installer
start_remote_job http://$WIN:8080/hudson/job/windows-installer "win installer"

# start the build of the Windows installer (ee)
start_remote_job http://$WIN:8080/hudson/job/windows-installer "win installer" ee

echo "Done."
