.. _apps.sdk.client.dev.editor.featuremanager:

Setting up a feature manager
============================

Editing with the Client SDK always starts with setting up a "feature manager". The API docs for the feature manager plugin can be found here:

http://suite.opengeo.org/opengeo-docs/sdk-api/lib/plugins/FeatureManager.html

Open up :file:`/src/app/app.js` in the application directory.  Add :file:`plugins/FeatureManager.js` to the list of dependencies at the top of the file.

.. code-block:: javascript

    * @require plugins/FeatureManager.js

Then search for the ``tools`` section and add the following:

.. code-block:: javascript

    {
        ptype: "gxp_featuremanager",
        id: "states_manager",
        paging: false,
        layer: {
            source: "local",
            name: "usa:states"
        }
    }

In the above case the feature manager is configured with a fixed layer. However, it's also possible to have the feature manager listen to the active (selected) layer in the layer tree. In the latter case, the feature manager would be configured without a layer and with ``autoSetLayer`` set to ``true``:

.. code-block:: javascript

    {
        ptype: "gxp_featuremanager",
        id: "states_manager",
        paging: false,
        autoSetLayer: true
    }

Reloading the application will not show any visible change.  This is because the feature manager is an invisble tool that can be used by other tools, such as the FeatureEditor. However, if we would open up Firebug we would see two additional requests: a SLD WMS DescribeLayer request, and a WFS DescribeFeatureType request for the configured layer.

