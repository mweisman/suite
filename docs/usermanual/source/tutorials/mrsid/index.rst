.. _dataadmin.mrsid:

Enabling MrSID image support
============================

The OpenGeo Suite comes with support for publishing data from many formats supported by the `Geospatial Data Abstraction Library <http://gdal.org>`_ (GDAL).  These formats include DTED, EHdr, AIG, ENVIHdr, and much more.

`MrSID <http://www.gdal.org/frmt_mrsid.html>`_ is available as an optional extension as well, but due to licensing issues, it is not enabled by default.  The steps to enable support will differ depending on how the OpenGeo Suite is installed, and on what platform.

Find the method of installation below and continue there.


Ubuntu
------

.. note:: By default, the Production WARs install will not show **any** of the GDAL image formats at all.  To enable GDAL image formats in the Production WAR file, please see the section on :ref:`dataadmin.gdal`.  This section assumes that the GDAL image formats are already enabled.

MrSID support is provided by a separate package called **gdal-mrsid**.

#. Install the following package from the OpenGeo repository.  (See :ref:`installation.linux.suite` for instructions on how to add the OpenGeo repository)::

      # apt-get install gdal-mrsid

   .. note::  You must run these commands as root or use :command:`sudo`.

#. Restart Tomcat.

Continue at the :ref:`dataadmin.mrsid.verify` section.


CentOS / Red Hat
----------------

.. note:: By default, the Production WARs install will not show **any** of the GDAL image formats at all.  To enable GDAL image formats in the Production WAR file, please see the section on :ref:`dataadmin.gdal`.  This section assumes that the GDAL image formats are already enabled.

MrSID support is provided by a separate package called **gdal-mrsid**.

#. Install the following package from the OpenGeo repository.  (See :ref:`installation.linux.suite` for instructions on how to add the OpenGeo repository)::

      # yum install gdal-mrsid

   .. note::  You must run this command as root or use :command:`sudo`.

#. Copy :file:`/usr/lib/gdal.jar` (:file:`/usr/lib64/gdal.jar` on 64-bit installs) to :file:`/usr/share/tomcat6/webapps/geoserver/WEB-INF/lib/gdal-1.8.1.jar`.

   .. note:: You may need to replace ``tomcat6`` with ``tomcat5`` in the above path depending on the server configuration.

#. Restart Tomcat.

Continue reading at the :ref:`dataadmin.gdal.verify` section.


Windows
-------

Windows Installer
~~~~~~~~~~~~~~~~~

MrSID support must be enabled during the installation process.

#. Navigate through the installer until you reach the :guilabel:`Choose Components` page.

   .. figure:: img/mrsid_win_components.png
      :align: center
      
      *The Components page of the Windows installer*

#. Scroll down to the :guilabel:`Extensions` option, and expand the tree.  Check the box for :guilabel:`MrSID`.

   .. figure:: img/mrsid_win_checked.png
      :align: center
      
      *Enabling the MrSID extension*


#. Click :guilabel:`Next` and continue the installation process. 

Continue reading at the :ref:`dataadmin.mrsid.verify` section.


Windows Production WARs
~~~~~~~~~~~~~~~~~~~~~~~

#. Navigate to:  http://data.opengeo.org/gdal_support/ .

#. Download the file that matches the version of the OpenGeo Suite (e.g. :file:`mrsid_win_25.zip` for version 2.5).

   .. note:: This file requires 32-bit Java/Tomcat.

#. Extract the contents of this archive to the location where the GDAL libraries were extracted (see :ref:`dataadmin.gdal`) 

#. Restart Tomcat.

Continue reading at the :ref:`dataadmin.gdal.verify` section.


Mac OS X
--------

Mac OS X Installer
~~~~~~~~~~~~~~~~~~

MrSID support is enabled during the installation process.

#. On the third page of the installer, click :guilabel:`Customize`.

   .. figure:: img/mrsid_mac_customize.png
      :align: center
      
      *Click Customize to select optional components*
      

#. On the next page, check the box that says :guilabel:`MrSID Support for OpenGeo Suite`, then click :guilabel:`Next`.

   .. figure:: img/mrsid_mac_check.png
      :align: center
      
      *Check this box to enable the MrSID extension*

#. Click :guilabel:`Next` and continue the installation process. 

If the OpenGeo Suite was installed without MrSID support, simply re-run the original installer again, following the steps above.  The extension will be added to the existing installation.

Continue reading at the :ref:`dataadmin.mrsid.verify` section.



Mac OS X Production WARs
~~~~~~~~~~~~~~~~~~~~~~~~

#. Navigate to:  http://data.opengeo.org/gdal_support/

#. Download the file that matches the version of the OpenGeo Suite (e.g. :file:`mrsid_osx_25.zip` for version 2.5).

#. Extract the contents of the archive into :file:`/usr/local/lib/`.

#. Restart Tomcat.

Continue reading at the :ref:`dataadmin.gdal.verify` section.




.. _dataadmin.mrsid.verify:

Verifying installation
----------------------

#. To verify that the MrSID extension was installed properly, navigate to the GeoServer web admin interface and log in with administrator credentials.

   .. note:: Please see the GeoServer reference documentation for more information about the GeoServer web admin interface.
   
#. Click on :guilabel:`Stores` and then :guilabel:`Add new Store`.  There should be a :guilabel:`MrSID` option under :guilabel:`Raster Data Formats`.

   .. figure:: img/mrsid_verify.png
      :align: center
      
      *Verifying that MrSID is an option in the Raster Data Sources*

