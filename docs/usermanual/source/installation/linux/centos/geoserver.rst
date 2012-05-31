.. _installation.linux.centos.geoserver:

Installing GeoServer on CentOS
==============================

The easiest way to install and set up GeoServer is by :ref:`installing the full OpenGeo Suite <installation.linux.suite>`.  The OpenGeo Suite comes complete with GeoServer as well as a full geospatial software stack, including utilities, data, and documentation.  That said, OpenGeo also provides individual packages for installing the components separately.

This page will describe how to install GeoServer on CentOS 5.  Earlier versions of CentOS are not supported at this time.


Access the OpenGeo repository
-----------------------------

OpenGeo provides a repository for packages in RPM format.  To access this repository, you need to first add the OpenGeo  repository to your local list of repositories.

.. note:: You will need to run these commands on an account with root access.

Change to the :file:`/etc/yum.repos.d` directory:

.. code-block:: bash

   cd /etc/yum.repos.d

Add the OpenGeo YUM repository.  The exact command will differ depending on whether you are using CentOS 5 or 6, and whether you are using a 32 bit installation or 64 installation.

.. list-table::
   :widths: 20 80
   :header-rows: 1

   * - System
     - Command
   * - CentOS 5, 32 bit
     - ``wget http://yum.opengeo.org/centos/5/i386/OpenGeo.repo``
   * - CentOS 5, 64 bit
     - ``wget http://yum.opengeo.org/centos/5/x86_64/OpenGeo.repo``
   * - CentOS 6, 32 bit
     - ``wget http://yum.opengeo.org/centos/6/i686/OpenGeo.repo``
   * - CentOS 6, 64 bit
     - ``wget http://yum.opengeo.org/centos/6/x86_64/OpenGeo.repo``

Package management
------------------

 Search for packages from OpenGeo:

.. code-block:: bash

   yum search opengeo

If the search command does not return any results, the repository was not added properly. Examine the output of the ``yum`` command for any errors or warnings.

Now you can install GeoServer.  The name of the package is :guilabel:`opengeo-geoserver`:

.. code-block:: bash

   yum install opengeo-geoserver


After installation
------------------

When completed, GeoServer will be installed as a servlet inside the local version of Tomcat.  Assuming that Tomcat is running on the default port 8080, you can verify that GeoServer is installed by navigating to the following URL::

   http://localhost:8080/geoserver/

This will load the Web Administration Interface.  Most management of GeoServer functionality can be done from this interface.

.. note:: The username and password for the GeoServer administrator account is **admin** / **geoserver**

For more information about running GeoServer, please see the `GeoServer Documentation <http://suite.opengeo.org/docs/geoserver/>`_
