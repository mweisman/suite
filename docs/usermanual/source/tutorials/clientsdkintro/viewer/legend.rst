.. _apps.sdk.client.dev.viewer.legend:

Adding a legend tool
====================

We will now add a legend to the bottom-left area of the viewer application. Open up the gxp API docs and search for a plugin that could provide legend functionality:

http://gxp.opengeo.org/master/doc/lib/plugins/Legend.html

The ``ptype`` to use is ``gxp_legend``. Open up :file:`app.js`, and configure this tool:

.. code-block:: javascript

    {
        ptype: "gxp_legend",
        actionTarget: "map.tbar"
    }

Also add this plugin to the list of dependencies at the top of :file:`app.js`. The file name is :file:`plugins/Legend.js`. Restart the web application and reload the browser. If no other configuration is done, there will be a button in the map's toolbar that will show a popup window with the legend of all visible layers in the viewer:

.. figure:: ../img/viewer_legendpopup.png
   :align: center


If we want the legend to be always present in the bottom left part of the application (below the layer tree), we will first create the Ext container in which the legend can be rendered. Open up :file:`app.js` again, and look for ``westpanel``. Replace the configuration of ``westpanel`` with:

.. code-block:: javascript

    {
        xtype: "container",
        layout: "vbox",
        region: "west",
        align: "stretch",
        pack: "start",
        defaults: {
            width: 200
        },
        width: 200,
        items: [{
            title: "Layers",
            id: "westpanel",
            flex: 1,
            layout: "fit"
        }, {
            xtype: "container",
            id: "legendpanel",
            layout: "fit",
            height: 250
        }]
    }

Change the configuration of the legend plugin to:

.. code-block:: javascript

    {
        ptype: "gxp_legend",
        outputTarget: "legendpanel"
    }

Now the legend will show up in the container with the id ``legendpanel`` :

.. figure:: ../img/viewer_legendpanel.png
   :align: center

