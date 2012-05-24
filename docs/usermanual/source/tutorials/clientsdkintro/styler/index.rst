.. _apps.sdk.client.dev.styler:

Adding a Styler plugin
======================

The Styler plugin makes it possible to edit layer styles. This uses the REST config module of the OpenGeo Suite's GeoServer.

.. note:: By default the Styler plugin will only work with the GeoServer of the same OpenGeo Suite instance where your app is deployed.

First, we need to prepare the build profile.  Open :file:`app.js` and add :file:`plugins/Styler.js` to the list of dependencies at the top of the file. 

.. code-block:: javascript

    * @require plugins/SnappingAgent.js

Search for the ``tools`` section, and add the Styler plugin:

.. code-block:: javascript

    {
        ptype: "gxp_styler"
    }

Restart the application and reload the browser:

.. figure:: ../img/styler_popup.png
   :align: center

There will now be a new button which will show a powerful and feature-rich :guilabel:`Styles` dialog. However, in order to edit colors, we still need to add a few things:

#. Copy over the :file:`script/ux/colorpicker` directory from GeoExplorer:

   * https://github.com/opengeo/GeoExplorer/tree/master/app/static/script/ux/colorpicker

#. Copy over the :file:`theme/ux/colorpicker` directory from GeoExplorer:

   * https://github.com/opengeo/GeoExplorer/tree/master/app/static/theme/ux/colorpicker

#. Open up :file:`index.html` in your app directory and add a stylesheet:

   .. code-block:: html

      <link rel="stylesheet" type="text/css" href="theme/ux/colorpicker/color-picker.ux.css" />

When done, restart the application and reload the browser.  It should now be possible to edit color in a styling rule:

  .. figure:: ../img/styler_popup_color.png
   :align: center
