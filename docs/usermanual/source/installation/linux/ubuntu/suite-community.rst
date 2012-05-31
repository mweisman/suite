.. _installation.linux.ubuntu.suite.community:

Installing OpenGeo Suite Community Edition on Ubuntu
====================================================

The commands contained in the following installation instructions must be run as a user with root privileges. 

.. note:: If you are upgrading from a previous version, jump to the section entitled :ref:`installation.linux.ubuntu.suite.community.upgrade`.

.. warning:: The APT packages are only available for Ubuntu 10.04 and above.

#. Begin by importing the OpenGeo GPG key:

   .. code-block:: bash

      wget -qO- http://apt.opengeo.org/gpg.key | apt-key add -

#. Add the OpenGeo APT repository:

   .. code-block:: bash

      echo "deb http://apt.opengeo.org/ubuntu lucid main" >> /etc/apt/sources.list
      
#. Update APT:

   .. code-block:: bash

      apt-get update

#. Search for packages from OpenGeo:

   .. code-block:: bash

      apt-cache search opengeo

   If the search command does not return any results, the repository was not added properly. Examine the output of the ``apt`` commands for any errors or warnings.

#. Install the OpenGeo Suite package (``opengeo-suite``):

   .. code-block:: bash

      apt-get install opengeo-suite

#. If the previous command returns an error, the OpenGeo repository may not have been added properly. Examine the output of the ``apt-get`` command for any errors or warnings.

#. During the installation process, you will be asked a few questions.  The first question is regarding the proxy URL that GeoServer is accessed through publicly.  This is only necessary if GeoServer is accessed through an external proxy.  If unsure, leave this field blank and just press ``[Enter]``.

#. You will then be prompted for the name of the default GeoServer administrator account.  Press ``[Enter]`` to leave it at the default of "admin", or type in a new name.

#. Next, you will be asked for the default GeoServer administrator password.  Press ``[Enter]`` to leave it at the default of "geoserver", or type in a new password.

#. You will be asked if you want to install OpenGeo Suite-specific PostGIS extensions.  Press ``[Enter]`` to accept.

#. If any other warning or dialog boxes show up, you can cycle through them by pressing ``[Alt-O]``.

#. You can launch the OpenGeo Suite Dashboard (and verify the installation was successful) by navigating to the following URL::

      http://localhost:8080/dashboard/

Continue reading at the :ref:`installation.linux.suite.details` section.

.. _installation.linux.ubuntu.suite.community.upgrade:

Upgrading
---------

#. Begin by updating APT:

   .. code-block:: bash

      apt-get update

#. Update the ``opengeo-suite`` package:

   .. code-block:: bash

      apt-get install opengeo-suite

Continue reading at the :ref:`installation.linux.suite.details` section.
