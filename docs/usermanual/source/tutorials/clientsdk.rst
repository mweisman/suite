.. _apps.clientsdk:

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

.. _apps.clientsdk.create:

Creating a new application
--------------------------

Navigate to the :file:`sdk` directory, then use the :command:`suite-sdk` command to create a new app called "myapp"::

  $ suite-sdk create path/to/myapp

The app will be placed in the :file:`path/to/myapp` directory.  This directory will contain all required client-side resources, including all JavaScript resources and a server for local testing.

.. _apps.clientsdk.debug:

Testing the application
-----------------------

From within the same :file:`sdk` directory, run the following command to launch a server that loads the application in "debug mode"::

  $ suite-sdk debug path/to/myapp

This server will publish the app at ``http://localhost:8080/`` .  Open this URL in a browser to see your application.

In debug mode, all JavaScript resources are loaded uncompressed to make for easy debugging in the browser.

Typing :command:`Ctrl-C` in the terminal window will shut down the server.

Changing the port
~~~~~~~~~~~~~~~~~

By default, the application will be published on port 8080.  To run the server on another 
port, add the :command:`-p <port>` parameter to the command::

  $ suite-sdk debug path/to/myapp -p 9090


Accessing GeoServer while testing
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When deploying the application in the OpenGeo Suite, the application will have access to GeoServer at the relative URL of :file:`/geoserver`.  To set up this same relationship while testing your application, you can proxy your remote Suite GeoServer to make it look as if it were available locally.  To proxy a GeoServer located at ``http://example.com:8080/geoserver``, run the following command::

  $ suite-sdk debug path/to/myapp -g http://example.com:8080/geoserver

This will make your 
remote GeoServer available locally at ``http://localhost:8080/geoserver`` .


.. _apps.clientsdk.deploy:

Deploying the application
-------------------------

To deploy your application to your (remote) OpenGeo Suite instance, run the following command from within the :file:`sdk` directory::

  $ suite-sdk deploy path/to/myapp -c tomcat5x -h http://example.com:8080 -p 8080 -u <username> -s <password>

In the above command, the :command:`-c tomcat5x` parameter sets the target server to be Tomcat 5.x.  The other parameters set the host, port, username, and password for the remote server.

.. note::  Type :command:`suite-sdk` without any arguments to see a full list of possible arguments.

When deploying the application, all JavaScript resources will be concatenated and minified.

