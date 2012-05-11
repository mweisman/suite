.. _apps.sdk.client.dev.basics:

Creating a viewer
=================

In this section we will create a viewer application using the client SDK.

Background
----------

The central object in the architecture of the client SDK is the **viewer** (``gxp.Viewer``). Primarily, a viewer combines a map panel with tools, but it can do much more. 

The main configuration options for a ``gxp.Viewer`` are:

.. list-table::
   :widths: 20 80
   :header-rows: 1

   * - Option
     - Description
   * - ``proxy``
     - Sets the proxy to use in order to bypass the Same Origin Policy when accessing remote resources through JavaScript. Only needed when external resources (e.g. from outside the OpenGeo Suite instance that your app lives in) are used. Will be set as ``OpenLayers.ProxyHost``.
   * - ``portalItems``
     - The items to add to the portal, in addition to the map panel that the viewer will create automatically.
   * - ``portalConfig``
     - Configuration object for the wrapping container (usually an ``Ext.Viewport``) of the viewer
   * - ``tools``
     - A set of tools that you want to use in the application, such as measure tools or a layer tree
   * - ``mapItems``
     - Any items to be added to the map panel, such as a zoom slider
   * - ``sources``
     - Configuration of layer sources available to the viewer, such as MapQuest or a WMS server
   * - ``map``
     - The configuration for the actual map part of the viewer, such as projection, layers, center and zoom

We will see many of these options in the following sections.

By default a viewer will fill up the whole viewport.

Setting up a new project
------------------------

We will use the ``suite-sdk`` script to set up a new viewer application called
"myviewer".

.. note:: For more information on the ``suite-sdk``, please see the section on :ref:`apps.sdk.client.script`.

Run the following commands::

  suite-sdk create /path/to/myviewer
  suite-sdk debug /path/to/myviewer

The application will run in debug mode on port 8080. If you want to run on a
different port, specify it with the :command:`-p` flag::

  suite-sdk debug /path/to/myviewer -p 9090

Now start up a browser, and type in the address of the application:

.. figure:: ../img/basic_viewer.png
   :align: center

What you get is a basic web mapping application which contains a layer tree, a map panel and some map tools. The map panel contains an OpenStreetMap base layer.

Dissecting the Viewer
---------------------

To examine how this project was set up, use your browser's debugging tool, e.g. Chrome/Safari Developer Tools or Firebug in Firefox.  Select the :guilabel:`Script` tab and look for the file :file:`app.js`

.. note:: You're looking for the second entry of :file:`app.js`; the first one is the loader file

.. figure:: ../img/basic_firebug.png
   :align: center

Dependency Management
---------------------

The first thing to see in :file:`app.js` is the list of JavaScript dependencies that are required to run the application. Whenever you add a component to the application, be sure to add a line to this file with the relative path to the file it is defined in, using the following pattern:

.. code-block:: javascript
    
   * @require path/to/Dependency.js

Everything listed here will be pulled in by the application build tool. The
result is a small application footprint, because only the required components
are included in the build.

.. note::  Whenever you add dependencies to :file:`app.js`, the debug server will need to be restarted.  To do so, go to the console where you ran :command:`suite-sdk`, hit Ctrl+C, and run the same command again

Application details
-------------------

In the example application, everything is wrapped by an anonymous function which is called when ``Ext.onReady`` fires. This is when the DOM is ready, i.e. when content can be added to a web page.

Our application creates a ``gxp.Viewer`` instance. The viewport is filled with a border layout, which has two items, a container in the 'west' region 200 pixels wide, and the map in the 'center' region. Please note that all tools in the client SDK are Ext plugins, so they can be created with a ``ptype`` shortcut in the config, similar to the ``xtype`` shortcut for Ext components. This viewer application defines the following tools:

* A **Layer Tree**, which will be rendered in the 'west' panel defined in the portalConfig.
* The **Add Layers** tool, a button that, when clicked, creates a dialog to add new layers to the map. This tool will be part of the top toolbar of the layer tree.
* The **Remove Layer** tool, which will be shown both in the top toolbar of the layer tree and in the context menu of the layer tree. This tool can be used to remove a layer from the map.
* The **Zoom to Extent** tool, which will be shown in the top toolbar of the map.  This can be used to zoom to the maximum extent of the map.
* The **Zoom** tool, which will create two buttons in the map top toolbar, to zoom in and zoom out with a factor 2 centered on the current map center.
* The **Navigation History** tool, which will create two buttons in the map's top toolbar, to navigate through visited map extents.

The viewer configuration defines two layer sources, a WMS-C (cacheable WMS) source to a local GeoServer (with the embedded GeoWebCache), and an OpenStreetMap source. Layer sources are also implemented as Ext plugins, so configured with a ``ptype``. The configuration for the map defines the initial map extent (centered on the USA) and the layers to load in the map, in this case an OSM base layer and the ``usa:states`` layer from an OpenGeo Suite's default GeoServer setup. If no local GeoServer can be found, this layer will not be loaded of course. Finally, a zoom slider is defined. Note that this can also be done using ``mapItems``.

In the above application, if there isn't a local GeoServer running, it is possible to proxy a remote GeoServer::

  suite-sdk debug /path/to/myviewer -g http://www.example.com/geoserver/ 

If this GeoServer has the ``usa:states`` layer, it will be added to the application:

.. figure:: ../img/basic_states.png
   :align: center

Next we will add more components to our app, and start with some basic viewer components.

