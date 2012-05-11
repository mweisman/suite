.. _apps.sdk.client.dev.viewer.projection:

Changing the projection of the viewer
=====================================

We will now change the viewer application to be in EPSG:4326 (WGS84) instead of the default Google Mercator. This will mean that our the two base layers (OSM and Google) are no longer available, so we will start by commenting them out in :file:`app.js`.  Next, we will reconfigure the map options in EPSG:4326, so remove (or comment out) the entries for ``projection``, ``units``, ``maxResolution``, ``maxExtent`` and ``center``, and add the following:

.. code-block:: javascript

    projection: "EPSG:4326",
    center: [-97, 38]

Reload the application in your browser:

.. figure:: ../img/viewer_projection_states.png
   :align: center

Now we can add a WMS of the world as our new base layer, first defining a new source in the sources section:

.. code-block:: javascript

    ol: {
        ptype: "gxp_olsource"
    }

This type is an "OpenLayers Source", which will allow us to define any OpenLayers Layer type. We want to configure an OpenLayers.Layer.WMS to a vmap0 tilecache instance, so add the following to the layers configuration:

.. code-block:: javascript

    {
        source: "ol",
        type: "OpenLayers.Layer.WMS",
        args: ["World map", "http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic'}],
        group: "background"
    }

This will result in:

.. figure:: ../img/viewer_projection_vmap.png
   :align: center

As a last step in our layer configuration, we will add a blank base layer to the application:

.. code-block:: javascript

    {
        source: "ol",
        type: "OpenLayers.Layer",
        args: ["Blank"],
        visibility: false,
        group: "background"
    }

The result will look like this:

.. figure:: ../img/viewer_projection_states_blank.png
   :align: center


