.. _apps.sdk.client.dev.editor.featureeditor:

Setting up a feature editor
===========================

Unlike the feature manager, the feature editor is a visible component in the viewer. Again, open :file:`app.js` in an editor and add :file:`plugins/FeatureEditor.js` to the list of dependencies.

.. code-block:: javascript

    * @require plugins/FeatureEditor.js

Then add the following to the ``tools`` section:

.. code-block:: javascript

    {
        ptype: "gxp_featureeditor",
        featureManager: "states_manager",
        autoLoadFeature: true
    }

Restart the server and reload the browser. Two new tools will be added to the toolbar: one to create new features, and one to modify existing features:

.. figure:: ../img/editor_featureeditor.png
   :align: center

Clicking on the :guilabel:`Edit existing feature` button, followed by clicking on a state in the map, will result in a popup:

.. figure:: ../img/editor_featureeditor_popup.png
   :align: center

Press the :guilabel:`Edit` button to edit the feature's geometry or the feature's attributes:

.. figure:: ../img/editor_featureeditor_edit.png
   :align: center

When done, press the :guilabel:`Save` button. Make sure that your GeoServer supports transactions (WFS-T) by checking that the Service Level is set to Transactional or Complete in the WFS page of the GeoServer admin tool.  The result will be saved using WFS-T.

.. note:: For production systems, editing shapefiles is not recommended.

