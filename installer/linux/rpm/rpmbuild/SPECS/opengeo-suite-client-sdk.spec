Name: opengeo-suite-client-sdk
Version: 2.5
Release: 1
Summary: The OpenGeo Suite Client SDK
Group: Applications/Development
License: see http://opengeo.org
Requires(post): bash
Requires(preun): bash
Requires: ant

%define _rpmdir ../
%define _rpmfilename %%{NAME}-%%{VERSION}-%%{RELEASE}.%%{ARCH}.rpm
%define _unpackaged_files_terminate_build 0

%description
The OpenGeo Suite Client SDK provides tools for building web mapping applications backed by the OpenGeo Suite.


%install
  rm -rf $RPM_BUILD_ROOT
  mkdir -p $RPM_BUILD_ROOT/usr/share/opengeo-suite/sdk
  cp -rp  $RPM_SOURCE_DIR/opengeo-suite-client-sdk/* $RPM_BUILD_ROOT/usr/share/opengeo-suite/sdk/.

%files
%defattr(-,root,root,-)
/usr/share/opengeo-suite/sdk/

