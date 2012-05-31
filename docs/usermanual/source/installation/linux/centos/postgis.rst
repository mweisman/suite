.. _installation.linux.centos.postgis:

Installing PostGIS on CentOS
============================

The easiest way to install and set up PostGIS is by :ref:`installing the full OpenGeo Suite <installation.linux.suite>`.  The OpenGeo Suite comes complete with GeoServer as well as a full geospatial software stack, including utilities, data, and documentation.  That said, OpenGeo also provides individual packages for installing the components separately.

This page will describe how to install PostGIS on CentOS 5.  Earlier versions are not supported at this time.

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

Now you can install PostGIS.  The name of the package is :guilabel:`opengeo-postgis`:

.. code-block:: bash

   yum install opengeo-postgis

After installation
------------------

When completed, PostGIS will be installed on your system as a service, running on port **5432**.  

.. note:: The username and password for the PostGIS administrator account is **opengeo** / **opengeo**

Testing connection
~~~~~~~~~~~~~~~~~~

To verify that PostGIS is installed properly, you can run the following command in a terminal (you will be prompted for a password):

.. code-block:: bash

   $ psql -Uopengeo -p5432 -c"SELECT postgis_full_version();" medford

If PostGIS is installed correctly, you should see information about the installed database.


pgAdmin III
~~~~~~~~~~~

The graphical management utility pgAdmin is included with the install.  To run pgAdmin, type :command:`pgadmin3` at a terminal, or navigate to :guilabel:`pgAdmin III` in the :guilabel:`Applications` menu.


For more information about running PostGIS, please see the `PostGIS Documentation <http://suite.opengeo.org/opengeo-docs/postgis/>`_

