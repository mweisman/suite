.. _apps.sdk.client.script:

Creating and deploying apps with the Client SDK
===============================================

The OpenGeo Suite Client SDK provides tools for building web mapping applications backed by 
the OpenGeo Suite.  The application development life-cycle are as follows:

#. **Creation** - Generating a new template application
#. **Customization** - Adding features and functionality to the template application
#. **Testing** - Deploying the application in a test mode to verify functionality and debug
#. **Deployment** - Deploying the application in a production environment

This tutorial will discuss creating, testing, and deploying of an application.

Prerequisites:

* The Client SDK should be already installed onto a machine where development will occur.
* This same machine should have `Apache Ant <http://ant.apache.org>`_ installed and on the path.  To verify this, type :command:`ant -version` at a terminal prompt.
* The instructions below assume that you have added the :file:`suite-sdk` (or :file:`suite-sdk.bat`) script from the :file:`bin` directory of the Client SDK to your system path.  This will allow you to run the ``suite-sdk`` command from anywhere on your filesystem.

.. _apps.clientsdk.create:

Creating a new application
--------------------------

The Client SDK comes with an application template that can be useful for getting started developing with the Suite.  To create a new application based on this template, run the :command:`suite-sdk create` command::

    suite-sdk create path/to/myapp

The app will be placed in the :file:`path/to/myapp` directory.  This directory will contain all required client-side resources for your application.

.. _apps.clientsdk.debug:

Testing the application
-----------------------

The Client SDK comes with a server that can be used to debug your application during development.  The server loads all of your JavaScript as individual, unminified scripts - very useful for debugging in a browser, but not suitable for production (the :command:`suite-sdk deploy` command concatenates and minifies your JavaScript for production).

Run the following command to launch a server that loads the application in "debug mode"::

    suite-sdk debug path/to/myapp

This server will publish the app at ``http://localhost:9080/`` .  Open this URL in a browser to see your application.

In debug mode, all JavaScript resources are loaded uncompressed to make for easy debugging in the browser.

Typing :command:`Ctrl-C` in the terminal window will shut down the server.

Changing the port
~~~~~~~~~~~~~~~~~

By default, the application will be published on port 9080.  To run the server on another port, provide the :command:`-p <port>` option to the debug command::

    suite-sdk debug -p 8000 path/to/myapp

This will make your application available for debugging at ``http://localhost:8000/``.


Accessing GeoServer while testing
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When deploying the application in the OpenGeo Suite, the application will have access to GeoServer at the relative URL of :file:`/geoserver`.  To set up this same relationship while testing your application, you can proxy your remote Suite GeoServer to make it look as if it were available locally.  To proxy a GeoServer located at ``http://example.com:8080/geoserver``, run the following command::

    suite-sdk debug -g http://example.com:8080/geoserver path/to/myapp 

This will make your remote GeoServer available locally at ``http://localhost:9080/geoserver`` .

Note that the :command:`-p` and :command:`-g` options can be used together.  For example, you could debug your application on port 8000 while accessing a local GeoServer running on port 8080::

    suite-sdk debug -p 8000 -g http://localhost:8080/geoserver path/to/myapp 

Again, this would make your application available at ``http://localhost:8000/`` while making GeoServer available on the same port at ``http://localhost:8000/geoserver``.

The debug server and proxy are suitable for debugging purposes only.  Use the :command:`suite-sdk deploy` command to prepare your application for production.

.. _apps.clientsdk.deploy:

Deploying the application
-------------------------

To deploy your application to your (remote) OpenGeo Suite instance, run the following command::

    suite-sdk deploy -d example.com  -u <username> -s <password> path/to/myapp

The above command assumes your Suite instance is available at ``http://example.com:8080/``.  Using the :command:`-u` and :command:`s` options you supply your remote manager credentials for the Suite.

.. note::  Type :command:`suite-sdk` without any arguments to see a full list of possible arguments.

When deploying the application, all JavaScript resources will be concatenated and minified.

