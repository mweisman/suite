.. _workflow.style:

Step 5: Style Your Layers with GeoExplorer
==========================================

.. note:: This section is optional, although recommended.  If you don't wish to style your layers, you can skip to the next section, :ref:`workflow.publish`.

GeoExplorer includes an integrated style editor for layers served through GeoServer.

.. note:: Styles in GeoServer are written in Styled Layer Descriptor (SLD).  Not all functionality referenced in the SLD 1.0 specification is available in the style editor.  For more information on SLD, please see the `OGC specification of SLD <http://www.opengeospatial.org/standards/sld>`_ and the GeoServer documentation.

#. Make sure the OpenGeo Suite is running and that GeoExplorer is open.

#. Authentication is required to edit styles through GeoExplorer. For security reasons, before editing capabilities are enabled, GeoExplorer needs to authenticate with GeoServer.  See the previous section (:ref:`workflow.edit`) on how to login to GeoExplorer.

   .. warning:: It is recommended to make a backup of your existing SLDs before editing styles in GeoExplorer.  Changes to styles will become live immediately on the server, and the SLDs will be completely overwritten with each edit, causing the code to possibly be restructured.  Please see the GeoServer documentation for details about how to access layer styles.

#. By default, no layers will be loaded.  Add the layer you wish to style, along with any other layers you may wish to use for context.  A basic distillation of the style rules will be displayed below the layer name in the :guilabel:`Overlays` panel.

   .. note::  For instructions on how to load a layer from your local GeoServer, see the section  :ref:`workflow.add`.

   .. figure:: img/style_layersadded.png
      :align: center

      *Layers added to GeoExplorer*

#. Click on a layer to select it.  The :guilabel:`Manage layer styles` button will become enabled (the palette icon).  Also, the :guilabel:`Edit Styles` option will be enabled in the layer context menu (accessed by right-clicking on the layer itself).

   .. note:: If the icon remains disabled, verify that you are logged into GeoExplorer.  Logging out and in, or just reloading GeoExplorer, can solve many simple issues.

   .. figure:: img/style_layerselected.png
      :align: center

      *Selecting a layer for styling*

#. Click on :guilabel:`Manage layer styles` (the palette icon) to bring up the style wizard.

   .. figure:: img/style_paletteicon.png
      :align: center

      *Selecting a layer for styling*

#. The wizard will display the current/active style, as well has a list of other available styles (if any).  Below this will be a list of rules applied to the layer.

   .. figure:: img/style_wizard.png
      :align: center

      *Style wizard*

#. Select a rule and click :guilabel:`Edit` below it.

   .. figure:: img/style_wizardeditbutton.png
      :align: center

      *Click to edit selected style rule*

#. Change the style as you see fit, selecting from symbol, size, color, opacity, filters, and many other options.

   .. note:: Please see the GeoExplorer Documentation for more about what can be styled here. 

   .. figure:: img/style_editbefore.png
      :align: center

      *Original style rule*

   .. figure:: img/style_colorpicker.png
      :align: center

      *Changing colors with the color picker*

   .. figure:: img/style_editafter.png
      :align: center

      *Changed style rule*

#. Click :guilabel:`Save`, and close the wizard to view the style changes on the map.

   .. figure:: img/style_finished.png
      :align: center

      *The newly styled layer*

#. Repeat this process for any layers that you wish to style.

