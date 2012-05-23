.. _apps.sdk.client.remotedeploy:

Preparing a Servlet Container for Remote Application Deployment
===============================================================

The servlet containers used with the OpenGeo Suite are configured to accept remote deployments of applications.  This means that one can develop an application locally, or just on some machine other than where the OpenGeo Suite is installed.  You can `download the SDK <http://opengeo.org/technology/sdk/>`_ from the OpenGeo website.

It is necessary to set a "manager" password on the OpenGeo Suite servlet container so it can accept a deployed application.  This process only needs only to be done once per server.  Subsequent application deployments will use the same credentials.

Once this process is completed, you can use the included SDK scripts to :ref:`deploy the application <apps.sdk.client.script.deploy>` to your instance of the OpenGeo Suite.

Windows hosts
-------------

The servlet manager password is set in the :file:`realm.properties` file in the :file:`etc` directory of the OpenGeo Suite installation (e.g. :file:`C:\\Program Files\\OpenGeo\\OpenGeo Suite\\etc\\`).  This file should have a line like the following::

  manager: mypassword,manager

This means that the username is "manager" (``manager:``), the password is ``mypassword``, and the account is valid for the "manager" group (``,manager``).

After this setting is made, restart the OpenGeo Suite.

OS X hosts
----------

The servlet manager password is set in the :file:`realm.properties` file in the :file:`/opt/opengeo/suite/etc/` directory.  This file would have a line like the following::

  manager: mypassword,manager

This means that the username is "manager" (``manager:``), the password is ``mypassword``, and the account is valid for the "manager" group (``,manager``).

After this setting is made, restart the OpenGeo Suite.

Linux packages and Production WARs
----------------------------------

When using Tomcat, the servlet manager password is set in the :file:`tomcat-users.xml` file. On Debian-based distributions (e.g. Ubuntu) with Tomcat 6, this file can be found in :file:`/etc/tomcat6/`.

Add the following information to the :file:`tomcat-users.xml` file.  You may need to create the file if it doesn't already exist:

.. code-block:: xml

   <tomcat-users>
     <role rolename="manager"/>
     <user username="manager" password="mypassword" roles="manager"/>
   </tomcat-users>

The above sets up a user with user name ``manager`` and password ``mypassword``, in the role of ``manager``.

After this setting is made, restart the servlet container.

