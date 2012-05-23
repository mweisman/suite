.. _workflow.load:

Step 1: Load Your Data Using PostGIS
====================================

.. note:: If you'd like to serve data directly from shapefiles, you can skip to the next section, :ref:`workflow.import`.

The first step of any workflow is to load your data into the OpenGeo Suite.  For the purposes of this workflow, we will assume that your initial data is stored as shapefiles, although there are many types of data formats that are compatible with the OpenGeo Suite.

#. Launch the OpenGeo Suite :ref:`dashboard` and Start the OpenGeo Suite, if you have not already done so. You can do this by clicking on the :guilabel:`Start` button in the Dashboard.

  .. note:: Linux users will need to start the OpenGeo Suite manually from the terminal.  From the root of the installation, run::

        ./opengeo-suite start

#. Click on the :guilabel:`Import Shapefiles` link which can be found by dragging your mouse down to the PostGIS section under :guilabel:`Components`.  This will load :guilabel:`pgShapeLoader` which will allow you to convert shapefiles to a tables in a PostGIS database.

   .. note:: Linux users should run :command:`shp2pgsql-gui` from the terminal.

   .. figure:: img/load_dashboard_pgshp.png
      :align: center

      *Import shapefiles link*

#. Next, click on the box that is titled :guilabel:`Shape File.`

   .. figure:: img/load_pgshp.png
      :align: center

      *pgShapeLoader*

#. In the folder dialog that appears, navigate to the location of your first shapefile, select it, then click :guilabel:`Open`.

   .. figure:: img/load_pgshp_select.png
      :align: center

      *Selecting a shapefile to import*

#. Next, fill out the form.

   .. note:: There are different form values depending on how the OpenGeo Suite was installed.

   .. list-table::
      :header-rows: 1

      * - Field
        - Windows/Mac
        - Linux
      * - **Username**
        - postgres
        - opengeo
      * - **Password**
        - [blank]
        - opengeo
      * - **Server Host**
        - localhost
        - localhost
      * - **Port**
        - **54321**
        - **5432**
      * - **Database**
        - [your user name on your computer]
        - [your user name on your computer]
      * - **SRID**
        - Projection code for your shapefile
        - Projection code for your shapefile

   .. warning:: Do not forget to enter the projection code in the **SRID** field.  The default value of -1 will need to be changed.  If you don't know the projection code (sometimes also known as SRS, CRS, or EPSG code) see the next section on :ref:`workflow.load.projection`.

   .. figure:: img/load_pgshp_form.png
      :align: center

      *Ready to import*

#. When ready, click :guilabel:`Import`.

   .. figure:: img/load_pgshp_importing.png
      :align: center

      *A successful import*

#. The shapefile has been imported as a table in your PostGIS database.  Repeat the same process for any additional shapefiles.

.. _workflow.load.projection:

Determining projections
-----------------------

.. note:: For a workaround that eliminates the need to find the shapefile projection, you can import shapefiles directly into GeoServer.  Please skip to the :ref:`workflow.import` section for details.

There are multiple ways to determine the projection code for a shapefile if it is not known.  You can look at metadata, search the source site, convert your ``.prj`` file into a code using `Prj2EPSG <http://prj2epsg.org>`_, or search `spatialreference.org <http://spatialreference.org>`_.

Metadata
~~~~~~~~

Shapefiles often have a metadata file included with it.  This metadata file can include information about the data contained in the shapefile, including the projection.  Look for an ``.xml`` file or ``.txt`` file among your shapefile collection and open this file in a text editor.  The projection will usually be a numerical code, possibly with a text prefix.  Examples:  "EPSG:4326" "EPSG:26918" "900913"

Search the source site
~~~~~~~~~~~~~~~~~~~~~~

Data download sites usually display information about the shapefiles on the site itself, sometimes on a page called "metadata" or "information about this data".  The projection will usually be a numerical code, possibly with a text prefix.  Examples:  "EPSG:4326" "EPSG:26918" "900913"

Convert using Prj2EPSG
~~~~~~~~~~~~~~~~~~~~~~

`Prj2EPSG <http://prj2epsg.org>`_ is a simple service for converting well-known text projection information from ``.prj`` files into standard EPSG codes. Shapefiles are comprised of multiple files, each with different extensions (``.shp``, ``.shx``, ``.prj`` among others). To use Prj2EPSG, open the ``.prj`` file with your text editor and paste the first block of text, known as `well-known text` or "WKT", into the text box.  Alternately, simply upload the ``.prj`` file using the upload dialog and click :guilabel:`Convert`.

   .. figure:: img/load_prj2epsg.png
      :align: center

      *The Prj2EPSG results screen*

Search spatialreference.org
~~~~~~~~~~~~~~~~~~~~~~~~~~~

`spatialreference.org <http://spatialreference.org>`_ is a web site that offers information on projections.  You can use the site's search box to help determine the projection for your shapefile.

Open the file with the ``.prj`` file in a text editor.  This file contains the technical details of the projection.  Copy the first block of text inside quotes and paste it into the search box of spatialreference.org .  Assuming a match, the site will return the likely projection code.  If the first text block fails, try the next block of text inside quotes.  Repeat this process if necessary to obtain the likely projection code.

Workaround
~~~~~~~~~~

If you are still unable to find the projection, you can instead load your shapefiles directly into GeoServer, bypassing PostGIS.  GeoServer may be able to intelligently determine the proper projection.  See the :ref:`workflow.import` section for details.

Verifying data
--------------

To verify that your data was loaded properly, you can use :guilabel:`pgAdmin`, a desktop interface tool for managing your PostGIS database.

#. Launch pgAdmin by clicking the :guilabel:`PostGIS` link in the Dashboard.  The following screen will display.

   .. figure:: img/load_pgadmin.png
      :align: center

      *pgAdmin*

   .. note:: **Linux users:**  pgAdmin will not be available through the Dashboard.  Please run :guilabel:`pgAdmin III` from the Applications menu in your GUI, or via :command:`pgadmin3` from the terminal. 

#. Double click on the server instance called :guilabel:`PostGIS (localhost:54321)` in the Object Browser.

   .. note::

      * **Windows/Mac users:**  If you are asked for a password, you can leave it blank.
      * **Linux users:**  The server will be running on port 5432.  The password is ``opengeo``.

#. Expand the tree to view :menuselection:`Databases -> [username] -> Schemas -> public -> Tables`.  You should see a listing of tables corresponding to the shapefiles that you loaded.

   .. note:: There will be two extra tables in the list, :guilabel:`geometry_columns`, and :guilabel:`spatial_ref_sys`.  Those two tables are automatically created by PostGIS.

   .. figure:: img/load_pgadmin_table.png
      :align: center

      *Database table listing*

For more information about pgAdmin and PostGIS, please see the PostGIS Documentation. You can access this by clicking the :guilabel:`PostGIS Documentation` link in the :ref:`dashboard`, or online at http://suite.opengeo.org/opengeo-docs/postgis/ .
