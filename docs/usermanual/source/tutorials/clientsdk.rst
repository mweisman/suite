.. _apps.sdk.client.script:

Creating and deploying apps with the Client SDK
===============================================

The OpenGeo Suite Client SDK provides tools for building web mapping applications backed by 
the OpenGeo Suite.  The application development life-cycle are as follows:

#. **Creation** - Generating a new template application
#. **Customization** - Adding features and functionality to the template application
#. **Testing** - Deploying the application in a test mode to verify functionality and debug
#. **Deployment** - Deploying the application in a production environment

This tutorial will discuss creating, testing, and deploying of an application.  See the tutorial :ref:`apps.sdk.client.dev` for information on customizing your application.

Prerequisites:

* The Client SDK should be installed onto a machine where the development will occur.  This is typically bundled with the OpenGeo Suite itself (under the :file:`sdk` subdirectory), but is available for download separately from http://opengeo.org/technology/sdk/ .
* This machine requires a `Java Development Kit (JDK) <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_.  A standard Java Runtime Environment (JRE) is not sufficient.
* This machine also requires `Apache Ant <http://ant.apache.org>`_ installed and on the path.  To verify this, type ``ant -version`` at a terminal prompt.
* The instructions below assume that you have added the :file:`bin` directory of the Client SDK to your system path.  This will allow you to run the ``suite-sdk`` command from anywhere on your file system.

.. _apps.sdk.client.script.install:

SDK Installation
----------------

The SDK doesn't require installation.  If the archive was downloaded from http://opengeo.org/technology/sdk/, it only needs to be extracted to a suitable place (such as :file:`/opt/opengeo/suite/sdk` or :file:`C:\\Program Files\\OpenGeo\\OpenGeo Suite\\sdk`).

Once extracted, the script used will be inside the :file:`bin` directory and is named :file:`suite-sdk` (:file:`suite-sdk.cmd` on Windows).

.. _apps.sdk.client.script.create:

Creating a new application
--------------------------

The Client SDK comes with an application template that can be useful for getting started developing with the Suite.  To create a new application based on this template, run the ``suite-sdk create`` command::

  suite-sdk create path/to/myapp

In the above command, the app will be called :file:`myapp` and will be placed in the :file:`path/to/myapp` directory.  This directory will contain all required client-side resources for your application.

.. warning:: Be sure to not name your application :file:`geoserver`, :file:`geoexplorer`, :file:`manager`, or any other name that might cause a conflict when :ref:`apps.sdk.client.script.deploy`.

.. _apps.sdk.client.script.debug:

Testing the application
-----------------------

The Client SDK comes with a server that can be used to debug your application during development.  The server loads all of your JavaScript as individual, unminified scripts - very useful for debugging in a browser, but not suitable for production.

Run the following command to launch a server that loads the application in "debug mode"::

  suite-sdk debug path/to/myapp

This server will publish the app at ``http://localhost:9080/`` .  Open this URL in a browser to see your application.

In debug mode, all JavaScript resources are loaded uncompressed to make for easy debugging in the browser.

Typing :command:`Ctrl-C` in the terminal window will shut down the server.

Changing the port
~~~~~~~~~~~~~~~~~

By default, the application will be published on port 9080.  To run the server on another port, provide the ``-l <port>`` option to the debug command::

  suite-sdk debug -l 8000 path/to/myapp

This will make your application available for debugging at ``http://localhost:8000/``.

Accessing GeoServer while testing
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When deploying the application in the OpenGeo Suite, the application will have access to GeoServer at the relative URL of :file:`/geoserver`.  If a GeoServer is not available on that path, or to set up this relationship with a remote GeoServer while testing your application, you can proxy a remote GeoServer to make it look as if it were available locally.  To proxy a GeoServer located at ``http://example.com:8080/geoserver``, run the following command::

  suite-sdk debug -g http://example.com:8080/geoserver path/to/myapp 

This will make your remote GeoServer available locally to your application at the relative URL of :file:`/geoserver`.

Note that the ``-l`` and ``-g`` options can be used together.  For example, you could debug your application on port 8000 while accessing a local GeoServer running on port 8080::

  suite-sdk debug -l 8000 -g http://localhost:8080/geoserver path/to/myapp 

Again, this would make your application available at ``http://localhost:8000/`` while making GeoServer available on the same port at ``http://localhost:8000/geoserver``.

The debug server and proxy are suitable for debugging purposes only.  Use the ``suite-sdk deploy`` command to prepare your application for production.

.. _apps.sdk.client.script.deploy:

Deploying the application
-------------------------

Deploying your application is the process of publishing an application on an OpenGeo Suite instance.  This process will concatenate and minify all JavaScript resources, and then copy them to a remote OpenGeo Suite.

To deploy your application to your (remote) OpenGeo Suite instance, run the following command::

  suite-sdk deploy -d example.com -r 8080 -u <username> -p <password> -c <container> path/to/myapp

.. note::  Type ``suite-sdk deploy --help`` without any arguments to see a full list of possible arguments.

The above command assumes your Suite instance is available at ``http://example.com:8080/``.  Using the ``-u`` and ``p`` options supplies the :ref:`remote manager credentials <apps.sdk.client.remotedeploy>` for the remote OpenGeo Suite.

The container type also needs to be supplied by the ``-c`` flag (default is ``tomcat6x``).  See the following table for the default containers for the various installation types.

.. list-table::
   :header-rows: 1

   * - Installation type
     - Container used
     - Syntax
   * - Ubuntu
     - Tomcat 6
     - ``-c tomcat6x``
   * - Red Hat / CentOS 5
     - Tomcat 5
     - ``-c tomcat5x``
   * - Red Hat / CentOS 6
     - Tomcat 6
     - ``-c tomcat6x``
   * - Windows installer
     - Jetty 6
     - ``-c jetty6x``
   * - Mac OS X installer
     - Jetty 6
     - ``-c jetty6x``

For a full list of supported containers and their deployment syntax, please see http://cargo.codehaus.org .
