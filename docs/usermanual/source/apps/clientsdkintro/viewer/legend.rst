.. _apps.sdk.client.dev.viewer.legend:

Adding a legend tool
====================

We will now add a legend to the bottom-left area of the viewer application. Open up the `API documentation <http://suite.opengeo.org/opengeo-docs/sdk-api>`_ and search for a plugin that could provide legend functionality:

http://suite.opengeo.org/opengeo-docs/sdk-api/lib/plugins/Legend.html

The ``ptype`` to use is ``gxp_legend``. Open up :file:`app.js`, and configure this tool:

.. code-block:: javascript

    {
        ptype: "gxp_legend",
        actionTarget: "map.tbar"
    }

Also add this plugin to the list of dependencies at the top of :file:`app.js`.  The file name is :file:`plugins/Legend.js`.

.. code-block:: javascript

    * @require plugins/Legend.js

Restart the web application and reload the browser. If no other configuration is done, there will be a button in the map's toolbar that will show a popup window with the legend of all visible layers in the viewer:

.. figure:: ../img/viewer_legendpopup.png
   :align: center

If we want the legend to be always present in the bottom left part of the application (below the layer tree), we will first create the Ext container in which the legend can be rendered. Open up :file:`app.js` again, and look for ``westpanel``. Replace the block that contains the configuration of ``westpanel``:

.. code-block:: javascript

    {
        id: "westpanel",
        xtype: "container",
        layout: "fit",
        region: "west",
        width: 200
    }

with:

.. code-block:: javascript

    {
        id: "westcontainer",
        xtype: "container",
        layout: "vbox",
        region: "west",
        width: 200,
        defaults: {
            width: "100%",
            layout: "fit"                
        },
        items: [{
            title: "Layers",
            id: "westpanel",
            border: false,
            flex: 1
        }, {
            id: "legendpanel",
            height: 250
        }]
    }

Next, change the configuration of the legend plugin to:

.. code-block:: javascript

    {
        ptype: "gxp_legend",
        outputTarget: "legendpanel"
    }

Now after reloading the server and application, the legend will show up in the container with the id ``legendpanel`` :

.. figure:: ../img/viewer_legendpanel.png
   :align: center

