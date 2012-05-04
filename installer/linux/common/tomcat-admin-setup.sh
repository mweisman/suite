#!/bin/bash
# postinst script for tomcat-admin/opengeo-suite
#

setup_tomcat_admin() {
  user=$1
  pass=$2
  tomcat_user_xml="<?xml version='1.0' encoding='utf-8'?>\
<tomcat-users>\
  <role rolename='manager'/>\
  <user username='$user' password='$pass' roles='manager'/>\
</tomcat-users>"
  (cd $TC_USERS_LOC;
  if [ -f $TC_USERS_FILE ]; then
    mv tomcat-users.xml tomcat-users.xml.old
  fi
  echo $tomcat_user_xml >> tomcat-users.xml)
  service $TC_VER restart
}

menu() {
  # default login values
  user="manager"
  pass="manager"
  while [ true ]; do
    printf "
    Tomcat Manager Configuration for OpenGeo Suite.

    Select an entry from the following list:
    ----------------------
    1. Manager username        : $user
    2. Manager password        : $pass

    9. Accept and continue
    0. Abort and quit

    choice: "

    read menuchoice

    case "$menuchoice" in
      "1")
        printf "Please choose a username for the tomcat manager.\n"
        respond "username" "$user" "3"
        user=$choice
        ;;

      "2")
        printf "Please choose a password for the tomcat manager.\n"
        respond "password" "$pass" "3"
        pass=$choice
        ;;

      "9")
        printf "Saving changes...\n"
        setup_tomcat_admin $user $pass
        printf "You can now login to the tomcat manager as $user with password $pass.\n"
        exit 0
        ;;

      "0")
        echo "Aborting, changes not saved."
        exit 255
        ;;
    esac
  done
}

respond() {
  printf "$1: [$2] "
  read choice
  if [  "$choice" = "" ]; then
    choice=$2
  fi
}

check_yn_option() {
  RESP=$1
  while [ true ]; do
    RESP=`echo $RESP | tr '[A-Z]' '[a-z]'`
    if [ "$RESP" == "y" ] || [ "$RESP" == "yes" ] || [ "$RESP" == "n" ] || [ "$RESP" == "no" ] ; then
      break
    fi
    printf "\nSorry, did not understand '$RESP'. Would you like to configure the tomcat manager for the OpenGeo Suite? [Y|n]: " 
    read RESP
  done

  if [ "$RESP" == "n" ] || [ "$RESP" == "no" ]; then
    exit 0
  else
    menu
  fi
}

md5_mismatch() {
  printf "It appears that you have modified your tomcat-users.xml ($TC_USERS_FILE). This may mean you have already setup a manager user."
  printf "\n\n"
  printf "Would you like to continue with setup? Your tomcat-users.xml will be backed up to $TC_USERS_FILE.old. [Y|n] "

  read MD5_RESP
  if [ -z "$MD5_RESP" ]; then
      MD5_RESP="y"
  fi
  check_yn_option $MD5_RESP
}

no_tc_users() {
  printf "WARNING: Could not find tomcat-users.xml. Please ensure that $TC_VER-admin-webapps is installed.\n"
  printf "Would you still like to configure the tomcat manager? [y|N]: "
  
  read NO_TC_RESP
  if [ -z "$NO_TC_RESP" ]; then
      NO_TC_RESP="n"
  fi
  check_yn_option $NO_TC_RESP
}

check_el_version() {
  # Get EL version
  EL_VERSION=`cat /etc/redhat-release | grep -Eio '[0-9]+\.[0-9]+' | cut -d '.' -f1`

  if [ "$EL_VERSION" == "5" ]; then
    TC_VER="tomcat5"
    BASE_TC_MD5="6d853f0f0dc589e9cf9ef248af1b0b0e"
  elif [ "$EL_VERSION" == "6" ]; then
    TC_VER="tomcat6"
    BASE_TC_MD5="fa582cf62e74b959529168fde18eedb1"
  else
    echo "Unknown RHEL compatibility version. The OpenGeo Suite is only supported on EL5 and EL6 compatible RHEL distros."
    exit 1
  fi
}

main() {
  # Only root can make needed changes to files
  if [ ! $( id -u ) -eq 0 ]; then
    echo "This script must be run as root. Exiting."
    exit 1
  fi

  if [ -f "/etc/redhat-release" ]; then
    check_el_version
  elif [ `lsb_release -is` == "Ubuntu" ]; then
    TC_VER="tomcat6"
    BASE_TC_MD5="73149380b771f15082b794ce8d88ffe1"
  else
    echo "Unknown distro. The OpenGeo Suite is supported on Red Hat Enterprise Linux 5 and 6, CentOS 5 and 6, and Ubuntu Linux 10.04 or later."
    exit 1
  fi

  # Check for existing tomcat-users.xml
  TC_USERS_LOC="/etc/$TC_VER"
  TC_USERS_FILE="$TC_USERS_LOC/tomcat-users.xml"
  if [ -f $TC_USERS_FILE ]; then
    LOCAL_TC_MD5=`md5sum $TC_USERS_FILE | cut -d ' ' -f1`
    if [ "$LOCAL_TC_MD5" != "$BASE_TC_MD5" ]; then
      md5_mismatch
    else
      menu
    fi
  else
    no_tc_users
  fi
}

main

exit 0
