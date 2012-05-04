.. _dataadmin.gdal:

Enabling GDAL image formats support
===================================

The OpenGeo Suite comes with support for publishing data from many formats supported by the `Geospatial Data Abstraction Library <http://gdal.org>`_ (GDAL).  These formats include DTED, EHdr, AIG, ENVIHdr, and much more.

GDAL image formats should be enabled by default when using the Windows installer, Mac OS X installer, or Linux packages.  GDAL image formats should require extra configuration only if using the **Production WAR** builds of the OpenGeo Suite.

.. note:: See the :ref:`installation` section for more information on the various ways to install the OpenGeo Suite

To enable GDAL image formats on the Production WAR installation, read at the correct target operating system below:


Ubuntu
------

#. Install the following packages from the OpenGeo repository.  (See HERE for instructions on how to add the OpenGeo repository)::

     # apt-get install gdal

  .. note::  You must run this command as root or use :command:`sudo`.

#. Once the package is installed, restart your servlet container to allow the changes to take effect::

     # service tomcat6 restart

Continue reading at the :ref:`dataadmin.gdal.verify` section.

CentOS / Red Hat
----------------

#. Install the following packages from the OpenGeo repository.  (See HERE for instructions on how to add the OpenGeo repository)::

     # yum install gdal

   .. note::  You must run this command as root or use :command:`sudo`.

#. Copy :file:`/usr/lib/gdal.jar` (:file:`/usr/lib64/gdal.jar` on 64-bit installs) to :file:`   /usr/share/tomcat6/webapps/geoserver/WEB-INF/lib/gdal-1.8.1.jar`

   .. note:: You may need to replace ``tomcat6`` with ``tomcat5`` in the above path depending on the server configuration.

#. Once the package is installed, restart your servlet container to allow the changes to take effect::

   # service tomcat6 restart

Continue reading at the :ref:`dataadmin.gdal.verify` section.


Windows
-------

.. note:: This section is under construction.

Mac
---

.. note:: This section is under construction.


.. _dataadmin.gdal.verify:

Verifying installation
----------------------

#. To verify that the GDAL image formats were enabled properly, navigate to the GeoServer web admin interface and log in with administrator credentials.

   .. note:: Please see the GeoServer reference documentation for more information about the GeoServer web admin interface.
   
#. Click on :guilabel:`Stores` and then :guilabel:`Add new Store`.  There should be many raster image formats, such as DTED, EHdr, AIG, and ENVIHdr in the list of formats.

   .. figure:: img/gdal_verify.png
      :align: center
      
      *Verifying that MrSID is an option in the Raster Data Sources*