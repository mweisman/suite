.. _workflow.publish:

Step 6: Compose and Publish Your Map With GeoExplorer
=====================================================

With your layers :ref:`edited <workflow.edit>` and :ref:`styled <workflow.style>` you are at last ready to compose and publish your map.  Map composition involves loading all the layers you wish to publish, setting layer order, and the zoom level.  Once this is accomplished, you can save your map in a format that makes it easy to embed in any web page.

If you haven't already, make sure the OpenGeo Suite is running and that GeoExplorer is open.

Map composition
---------------

#. Add all of the layers to the map that you would like to publish.  This can include local GeoServer layers, remote WMS layers, and other hosted services such as MapQuest or Google.

#. In the Layers Panel, click and drag the layers up and down to adjust rendering order.  The layers that are on the top of the list will be drawn last (and thus "on top" of the other layers).

   .. figure:: img/publish_drag.png
      :align: center

      *Drag layers to reorder*

#. Use the zoom and pan tools to set the default map view.

   .. figure:: img/publish_compose.png
      :align: center

      *A finished map*


Save the map view
-----------------

You can save your map composition for later, to be accessed by a URL bookmark.

#. Click the :guilabel:`Save Map` button to display a URL.

   .. figure:: img/publish_savemapbutton.png
      :align: center

      *The Save Map button will generate a URL*

#. When navigating to this URL at a later time, the map will return to this exact placement.

   .. figure:: img/publish_savemap.png
      :align: center

      *Copy this URL to save your map*


Publish via embedded map
------------------------

GeoExplorer allows you to publish your map view by generating a small block of HTML code that can be embedded in any web page.

#. Click on the :guilabel:`Publish Map` icon.

   .. figure:: img/publish_publishmapbutton.png
      :align: center

      *The Publish Map button will generate HTML*

#. Choose the tools you would like to have available in your embedded map:

   .. figure:: img/publish_tools.png
      :align: center

      *Selecting the tools for the published map*

#. A dialog will show some HTML code that can be copied and included in a web page in order to embed the map.  You can change the values for the map size in the :guilabel:`Map Size` dropdown boxes, or by changing the :guilabel:`Height` and :guilabel:`Width` values.  The changes will automatically be reflected in the HTML.

   .. figure:: img/publish_html.png
      :align: center

      *HTML for embedding a map*

#. Copy and paste this HTML code into a web page to embed your map.

.. note:: URLs that contain "localhost" will not be accessible to anyone other than those on the local machine.  Please use a web server such as Apache or IIS to proxy the OpenGeo Suite onto a web-accessible URL.


For more information on GeoExplorer, please see the GeoExplorer Documentation, accessible through the :ref:`dashboard` locally, or online at http://suite.opengeo.org/opengeo-docs/geoexplorer .
