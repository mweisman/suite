.. _installation.linux.suite.details:

Installation details
====================

This section discusses some helpful information about the OpenGeo Suite, as well as how to access the various components after installation.


Starting/Stopping the OpenGeo Suite
-----------------------------------

GeoServer, GeoExplorer, and all other web-based containers including the documentation are installed into the existing Tomcat instance on the machine. Starting and stopping these applications are therefore accomplished by managing them through the standard Tomcat instance.  Tomcat is installed as a service under the name of :command:`tomcat6` (or possibly :command:`tomcat5`, depending on your system), and can be managed accordingly:

.. code-block:: bash

   /etc/init.d/tomcat6 start
   /etc/init.d/tomcat6 stop


PostGIS is also installed as a service, under the name of :command:`postgresql`, and can be managed in the same way as Tomcat:

.. code-block:: bash

   /etc/init.d/postgresql start
   /etc/init.d/postgresql stop

Both services are started and set to run automatically when the OpenGeo Suite is installed.


Accessing web applications
--------------------------

The easiest way to launch the web-based applications contained in the OpenGeo Suite is via the Dashboard.  All web applications are linked from this application.  The Dashboard is accessible via the following URL::

  http://localhost:8080/dashboard/

.. note:: You will need to change the port number if your Tomcat installation is serving on a different port.

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Application
     - URL
   * - OpenGeo Suite Dashboard
     - http://localhost:8080/dashboard/
   * - GeoServer
     - http://localhost:8080/geoserver/
   * - GeoExplorer
     - http://localhost:8080/geoexplorer/
   * - OpenGeo Suite Documentation
     - http://localhost:8080/opengeo-docs/
   * - OpenGeo Recipe Book
     - http://localhost:8080/recipes/

Accessing PostGIS
-----------------

You can access PostGIS in one of two ways:  via the command line will the :command:`psql` application, or via a graphical interface with the :command:`pgadmin3` application.  Both commands should be on the path and can be invoked from any Terminal window.  If unfamiliar with PostGIS, start with :command:`pgadmin3`.  

This version of PostGIS is running on port 5432, with administrator username and password **opengeo** / **opengeo**.


List of packages
----------------

Once installed, you will have the following packages installed on your system:

.. list-table::
   :widths: 20 20 60 
   :header-rows: 1

   * - Package
     - Name
     - Description
   * - ``opengeo-suite``
     - OpenGeo Suite
     - The full OpenGeo Suite and all its contents.  All packages listed below are installed as dependencies with this package.  Contains GeoExplorer, Styler, GeoEditor, Dashboard, Recipe Book, and more.
   * - ``opengeo-docs``
     - OpenGeo Suite Documentation
     - Full documentation for the OpenGeo Suite.
   * - ``opengeo-geoserver``
     - GeoServer
     - High performance, standards-compliant map and geospatial data server.
   * - ``opengeo-jai``
     - Java Advanced Imaging (JAI)
     - Set of Java toolkits to provide enhanced image rendering abilities.
   * - ``opengeo-postgis``
     - PostGIS
     - Robust, spatially-enabled object-relational database built on PostgreSQL.
   * - ``opengeo-suite-data``
     - OpenGeo Suite Data
     - Sample data for use with the OpenGeo Suite.
   * - ``pgadmin3``
     - pgAdmin III
     - Graphical client for interacting with PostgreSQL/PostGIS.
   * - ``opengeo-suite-ee`` (Enterprise Edition only)
     - OpenGeo Suite Enterprise Edition package
     - Enterprise Edition functions and libraries.  


System requirements
-------------------

The OpenGeo Suite has the following system requirements:

* Operating System: Ubuntu 10.04 and 10.10, CentOS 5-6
* Memory: 512MB minimum (1GB recommended)
* Disk space: 750MB minimum (plus extra space for any loaded data)
* Browser: Any modern web browser is supported.
* Permissions: Super user privileges are required for installation
