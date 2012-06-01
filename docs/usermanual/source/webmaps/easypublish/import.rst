.. _workflow.import:

Step 2: Import Your Data Using GeoServer
========================================

The next step is to import your data into GeoServer so it can be published.  GeoServer comes with a **Layer Importer** to make this process easy.  The Layer Importer can import data from shapefiles, PostGIS databases, and ArcSDE/Oracle Spatial databases (with appropriate extension files installed).

.. note:: This example workflow uses the PostGIS data from the previous step (:ref:`workflow.load`), however, if you skipped that step, you can import shapefiles here in much the same way.

#. First, make sure the OpenGeo Suite is running.  You can do this by clicking on the :guilabel:`Start` button in the :ref:`dashboard`.

#. Log in to GeoServer.  Enter your current username and password and click :guilabel:`Login`.  (The default username and password is ``admin`` and ``geoserver``.  The :ref:`dashboard.prefs` page in the Dashboard will have the most current credentials.)

   .. figure:: img/import_login.png
      :align: center

      *Logging in to the GeoServer admin interface*

#. Open the GeoServer Layer Importer.  You can do this by click on the :guilabel:`Import Data` link on the left column of the main menu of the GeoServer UI.

   .. figure:: img/import_gslink.png
      :align: center

      *Accessing the Layer Importer from the GeoServer UI*

   .. note:: The Layer Importer is also available through the Dashboard by clicking on :guilabel:`Import data`, next to GeoServer. 

       .. figure:: img/import_dashboard.png
          :align: center

          *Accessing the Layer Importer from the Dashboard*


#. Select :guilabel:`PostGIS` as the type of data you wish to import.

   .. note:: In order to enable ArcSDE and Oracle Spatial support in the Layer Importer, external files are required from your current database installation.  For ArcSDE, the files :file:`jsde*.jar` and :file:`jpe*.jar` are required.  For Oracle spatial, :file:`ojdbc*.jar` is required.  Copy the file(s) into :file:`webapps/geoserver/WEB-INF/lib` from the root of your installation, and then restart.  If successful, you will see extra options on this page.

   .. figure:: img/import_datasource.png
      :align: center

      *Importing from PostGIS*

#. Fill out the form.  Assuming the steps followed in the previous section (:ref:`workflow.load`) the form should be filled out as follows:

   .. note:: There are different form values depending on how the OpenGeo Suite was installed.

   .. list-table::
      :header-rows: 1

      * - Field
        - Windows/Mac
        - Linux
      * - **Connection Type**
        - Default
        - Default
      * - **Host**
        - localhost
        - localhost
      * - **Port**
        - 54321 
        - 5432 
      * - **Database**
        - [Username]
        - [Username]
      * - **Schema**
        - public
        - public
      * - **Username**
        - postgres
        - opengeo
      * - **Password**
        - [blank]
        - opengeo

#. [Optional] Select a workspace for your data.  The default workspace is :guilabel:`opengeo`.  If you wish to create a new workspace, click the :guilabel:`Add new...` link and type in a name (with no spaces).

#. [Optional] Select a store for your data.  The default store is called :guilabel:`postgis`, but it is possible to select :guilabel:`Create new` in the drop-down box.

#. When finished, click :guilabel:`Next`.

   .. figure:: img/import_form.png
      :align: center

      *Filled out importer form*

#. On the next screen, a list of spatial tables found in the database will be displayed.  This list should match the number of shapefiles loaded in the previous section.  When ready to continue, click :guilabel:`Import`.

  .. figure:: img/import_list.png
      :align: center

      *A listing of spatial layers found in the database*

#. The tables will be loaded as individual layers in GeoServer.  When finished, the results will be displayed.  If there were any errors (such as problems :ref:`workflow.load.projection`), they will be described in this list.  

   .. figure:: img/import_importing.png
      :align: center

      *The import in progress*

#. You can see a preview of how each layer looks in either OpenLayers, Google Earth, or Styler, by clicking the appropriate link.  If you would like to view a layer's configuration, click the Name of the layer.

If you wish to import data from other sources, you may repeat this process.
