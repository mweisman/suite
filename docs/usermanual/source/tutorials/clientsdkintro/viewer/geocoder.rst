.. _apps.sdk.client.dev.viewer.geocoder:

Adding a Google Geocoder search field
=====================================

.. warning::  Before adding Google components to your applications, make sure that Google's Terms of Use allow you to do so.

Open up the gxp API docs again, and search for a plugin that could provide the geocoder functionality.

http://gxp.opengeo.org/master/doc/lib/plugins/GoogleGeocoder.html

This requires the Google Maps v3 API to be present in the application. Add the following script tag in :file:`app/static/index.html`:

.. code-block:: html

    <script src="http://maps.google.com/maps/api/js?v=3.6&amp;sensor=false"></script>

Now open up :file:`app.js` again, and add the tool configuration for this plugin. We want the geocoder field to show up in the map's toolbar:

.. code-block:: javascript

    {
        ptype: "gxp_googlegeocoder",
        outputTarget: "geocoder",
        outputConfig: {
            emptyText: "Search for a location ..."
        }
    }

However to make this happen, we also need to add a tbar to the map's config, otherwise the container in which the geocoder combobox renders will not be there in time:

.. code-block:: javascript

    map: {
        id: "mymap", // id needed to reference map in portalConfig above
        tbar: {id: 'geocoder'},
        title: "Map",
        ..
    }

Add the plugin to the list of dependencies in :file:`app.js`. The file name is :file:`plugins/GoogleGeocoder.js`. Restart the application and reload the browser.  The Google geocoder will now be in our viewer:

.. figure:: ../img/viewer_geocoder.png
   :align: center

