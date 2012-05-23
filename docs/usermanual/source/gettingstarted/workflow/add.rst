.. _workflow.add:

Step 3: Add Layers To A Map With GeoExplorer
============================================

Now that your layer data is imported, you can organize and compose your layers into a map. **GeoExplorer** allows you to create web maps based on layers served through GeoServer and a variety of other sources.

Add Layers
----------

#. First, make sure the OpenGeo Suite is running.  You can do this by clicking on the :guilabel:`Start` button in the :ref:`dashboard`.

#. Run GeoExplorer by selecting :guilabel:`GeoExplorer` in the Dashboard.

   .. figure:: img/add_dashboard.png
      :align: center

      *Running GeoExplorer*

   .. figure:: img/add_geoexplorer.png
      :align: center

      *GeoExplorer*

#. When GeoExplorer has opened, you will see a base map of the world served from MapQuest. To add your layers on top of this base map, click on the :guilabel:`Add Layers` button (a green circle with a white plus) in the top left corner of the screen.

   .. figure:: img/add_add_button.png
      :align: center

      *Add Layers button in the Layers Panel toolbar*

#. In the `Available Layers` dialog, you can select the layers you wish to add to GeoExplorer. Just select the server from which you would like to load data in the box next to :guilabel:`View available data from`. The first default option this dialog offers is to show all of the layers served through your local instance of GeoServer.  If running GeoExplorer as a standalone module, GeoExplorer will instead display layers from a remote hosted GeoServer by default.

   .. note:: You can also add layers served through another GeoServer instance, or any other compatible Web Map Service (WMS).  See the :ref:`workflow.add.remotewms` section below.)

   .. figure:: img/add_add_dialog.png
      :align: center

      *Add Layers dialog*

#. A list of layers will be displayed.  For each layer, the layer :guilabel:`Title` and :guilabel:`Id` is displayed.  The layer :guilabel:`Abstract` (description) for a given layer can be displayed by clicking on the plus icon next to that layer. Select the layers you wish to add, then click :guilabel:`Add layers`.  (You can Ctrl/Cmd-click to select multiple layers.)

   .. figure:: img/add_add_dialog_selected.png
      :align: center

      *Selecting layers to add to your map*

#. When done, click :guilabel:`Done`. The layers will be added to the map, and you should now see your layers in the Overlays folder in the GeoExplorer Layers Panel.

   .. figure:: img/add_added.png
      :align: center

      *The initial view of layers*

GeoExplorer doesn't automatically zoom to your newly added layers.  If you don't see your layers in the Map Window, select a layer in the and click the :guilabel:`Zoom to layer extent` button in the GeoExplorer Layers Toolbar.  You can also right-click on a specific layer and select :guilabel:`Zoom to layer extent`.

   .. figure:: img/add_extent_menu.png
      :align: center

      *Zoom to Layer Extent entry in the Layers Panel toolbar*

   .. figure:: img/add_extent.png
      :align: center

      *Map zoomed to layer extent*

Don't worry about how your layers look now.  We will style them in :ref:`workflow.style`.




.. _workflow.add.servers:

Default servers
~~~~~~~~~~~~~~~

GeoExplorer initially recognizes a number of servers, each with their own list of layers.

    .. list-table::
       :header-rows: 1
       :widths: 20 40 40 

       * - Server Name
         - Description
         - Notes
       * - **Local GeoServer**
         - Local GeoServer WMS, if present on the same server as GeoExplorer.
         - Not available when using the standalone version of GeoExplorer.
       * - **Remote Suite GeoServer**
         - GeoServer WMS on OpenGeo's servers.
         - GeoServer URL is: ``http://v2.suite.opengeo.org/geoserver/``
       * - **MapQuest Layers**
         - Layers served through `MapQuest <http://mapquest.com>`_.
         - Available layers: 
             * :guilabel:`MapQuest OpenStreetMap` (**default**)
             * :guilabel:`MapQuest Imagery`
       * - **OpenStreetMap Layers**
         - Layers served through `OpenStreetMap <http://openstreetmap.org>`_.
         - Available layers: 
             * :guilabel:`Mapnik`
       * - **Google Layers**
         - Layers served through `Google Maps <http://maps.google.com>`_.
         - Available layers: 
             * :guilabel:`Google Roadmap`
             * :guilabel:`Google Satellite`
             * :guilabel:`Google Hybrid`
             * :guilabel:`Google Terrain`
       * - **Bing Layers**
         - Layers served through `Bing Maps <http://bing.com/maps>`_.
         - Available layers: 
             * :guilabel:`Bing Roads`
             * :guilabel:`Bing Aerial`
             * :guilabel:`Bing Aerial with Labels`
       * - **MapBox Layers**
         - Layers served through `MapBox <http://mapbox.com>`_.
         - Over a dozen layers available, including Blue Marble Topography, Natural Earth imagery, and stylized base layers like Geography Class.


To view/add layers from one of these servers, select the server name from the :guilabel:`View available data from:` box.

    .. figure:: img/add_servers.png
       :align: center

       *Available servers*


.. _workflow.add.remotewms:

Connect to another WMS
~~~~~~~~~~~~~~~~~~~~~~

To load layers from a server not listed above, you will first need to add it to the list of available servers.  You can add any valid `WMS <http://suite.opengeo.org/opengeo-docs/geoexplorer/glossary.html#term-wms>`_.

To add a new server, click on :guilabel:`Add a New Server` in the :guilabel:`Available Layers` dialog box.  A small window will pop up, where a WMS service URL can be entered.

   .. figure:: img/add_server_new.png
      :align: center

      *Adding a new WMS server*

You can enter a URL to a server's WMS endpoint or to a WMS `GetCapabilities <`http://suite.opengeo.org/opengeo-docs/geoexplorer/glossary.html#term-getcapabilities>`_ request.  An example URL of a WMS endpoint would be::

  http://suite.opengeo.org/geoserver/wms

An example URL of a WMS GetCapabilities request would be::

  http://suite.opengeo.org/geoserver/wms?service=wms&version=1.1.1&request=getcapabilities

Either of the above URLs would result in the same list of layers.

An example of a non-GeoServer WMS GetCapabilities request would::

  http://terraservice.net/ogccapabilities.ashx?version=1.1.1&request=GetCapabilities

Once the server has been added to the list, any layers served from that server can be added to GeoExplorer as described above.

Remove Layers
-------------

If you add a layer by mistake or simply wish to remove a layer, layers can be removed from GeoExplorer in two ways. 

#. Select the layer by clicking on it in the `GeoExplorer Layers Panel <http://suite.opengeo.org/opengeo-docs/geoexplorer/workspace.html#geoexplorer-workspace-layerspanel>`_, then click the :guilabel:`Remove Layer` button in the Layer Panel toolbar, or right-click and select :guilabel:`Remove Layer` in the context menu.  You can remove Base Layers and Overlays in the same way.

   .. figure:: img/add_remove_button.png
      :align: center

      *Remove Layer button*

   .. figure:: img/add_remove_menu.png
      :align: center

      *Remove Layer entry in the Layers Panel toolbar*

.. note:: You can only select one layer at a time. 	


