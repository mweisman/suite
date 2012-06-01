.. _workflow.edit:

Step 4: Edit Your Data With GeoExplorer
=======================================

.. note:: This step is optional.  If you don't wish to edit any of your existing data, you can skip to the next section, :ref:`workflow.style`.

GeoExplorer allows for editing of geospatial data served through a local copy of GeoServer.  We will use GeoExplorer to create and edit features in our data.

.. note:: Editing data served directly from shapefiles is not recommended.

#. Make sure the OpenGeo Suite is running and that GeoExplorer is open.

#. If you haven't already done so, add a layer that you wish to edit.  See the previous step, :ref:`workflow.add` for details.

#. In order to edit data, we will need to log in to GeoExplorer, which uses the same credentials as GeoServer.  Click the Login button at the top right of the screen.

   .. figure:: img/edit_login_button.png
      :align: center

      *Login button*

#. Enter your username and password.

   .. note:: By default, the username and password is **admin** and **geoserver**.  The :ref:`dashboard.prefs` in the Dashboard will show the current credentials.

   .. figure:: img/edit_login.png
      :align: center

      *Logging in to GeoExplorer*

#. Once you are logged in, the top right of the screen will show this:

   .. figure:: img/edit_loggedin.png
      :align: center

      *This will show that you're logged in*

#. Now select the layer you wish to edit by clicking on it in the Layers Panel on the left side of the screen.  A few buttons on the toolbar will become enabled, specifically the :guilabel:`Create new feature` button, and the :guilabel:`Edit existing feature` button.




Editing attribute data
----------------------

#. To edit attribute data for features in the layer, first click on the :guilabel:`Edit existing feature` button in the menu bar.  

   .. figure:: img/edit_edit_button.png
      :align: center

      *Edit existing feature button*

#. Click on a feature.  A popup will display, showing the attributes of this feature.

   .. figure:: img/edit_attribute.png
      :align: center

      *Displaying attributes for a feature*

#.  Click the :guilabel:`Edit` button and then click on any of the fields to change the value.

   .. figure:: img/edit_attribute_edit.png
      :align: center

      *Editing attributes*

#. When done, click :guilabel:`Save`.  Your attribute data will be saved through GeoServer into the original source data format.

   .. figure:: img/edit_attribute_saved.png
      :align: center

      *Edited attribute saved*


Creating a new feature
----------------------

#. To create a new feature in a layer, first make sure that the layer is selected, then click the :guilabel:`Create a new feature` button in the menubar the top of the screen.

   .. figure:: img/edit_create_button.png
      :align: center

      *Create new feature button*

#. Click anywhere in the map window to start drawing the feature.  This workflow will vary depending on whether the layer consists of points, line and polygons.  Clicking on the map will add new vertices.  Clicking and dragging existing vertices will move them.  Holding the shift button while dragging will draw smooth curves instead of points.

   .. figure:: img/edit_create_draw.png
      :align: center

      *Drawing a new feature*

#. Double-click when done.  A popup will display, where attribute data can then be entered.


   .. figure:: img/edit_create_attribute.png
      :align: center

      *Editing the attributes of a new feature*

#. When finished, click :guilabel:`Save`.  Your new feature will become part of the layer.

   .. figure:: img/edit_create_saved.png
      :align: center

      *New feature saved*

Editing an existing feature's geometry works very similarly to creating a new one.  Simply click on the :guilabel:`Edit existing feature` button, click on an existing feature, and click :guilabel:`Edit` in the popup that displays.  The vertices of the feature will become editable.  Double-click to finish.
 
   .. figure:: img/edit_edit_feature.png
      :align: center

      *New feature saved*


Deleting a feature
------------------

.. warning:: This action is not undoable.

#. To delete a feature, click on the :guilabel:`Edit Existing Feature` button in the menu bar.

   .. figure:: img/edit_edit_button.png
      :align: center

      *Edit Existing Feature button*

#. Click on a feature.  A popup will display, showing the attributes of this feature.  Click the :guilabel:`Delete` button.

   .. figure:: img/edit_delete.png
      :align: center

      *Deleting a feature*

#. A confirmation popup will display.  Click :guilabel:`Yes` to confirm deletion.

   .. figure:: img/edit_delete_confirm.png
      :align: center

      *Confirmation for deleting a feature*

The feature will be removed from the layer.


