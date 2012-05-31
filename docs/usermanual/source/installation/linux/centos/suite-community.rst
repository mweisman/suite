.. _installation.linux.centos.suite.community:

Installing OpenGeo Suite Community Edition on CentOS
====================================================

The commands contained in the following installation instructions must be run as a user with root privileges. 

.. note:: If you are upgrading from a previous version, jump to the section entitled :ref:`installation.linux.centos.suite.community.upgrade`.

.. warning:: The RPM packages are only compatible with CentOS 5 and above.

#. Change to the :file:`/etc/yum.repos.d` directory:

   .. code-block:: bash

      cd /etc/yum.repos.d

#. Add the OpenGeo YUM repository.  The exact command will differ depending on whether you are using CentOS 5 or 6, and whether you are using a 32 bit installation or 64 installation.

   .. list-table::
      :widths: 20 80
      :header-rows: 1

      * - System
        - Command
      * - CentOS 5, 32 bit
        - ``wget http://yum.opengeo.org/centos/5/i386/OpenGeo.repo``
      * - CentOS 5, 64 bit
        - ``wget http://yum.opengeo.org/centos/5/x86_64/OpenGeo.repo``
      * - CentOS 6, 32 bit
        - ``wget http://yum.opengeo.org/centos/6/i686/OpenGeo.repo``
      * - CentOS 6, 64 bit
        - ``wget http://yum.opengeo.org/centos/6/x86_64/OpenGeo.repo``

#. Update the YUM databases:

   .. code-block:: bash

      yum update

#. Now we are ready to install the OpenGeo Suite.  The package is called ``opengeo-suite``:

   .. code-block:: bash

      yum install opengeo-suite

#. If the previous command returns an error, the OpenGeo repositories may not have been added properly. Examine the output of the ``yum`` command for any errors or warnings.

#. You can launch the OpenGeo Suite Dashboard (and verify the installation was successful) by navigating to the following URL::

      http://localhost:8080/dashboard/

Continue reading at the :ref:`installation.linux.suite.details` section.


.. _installation.linux.centos.suite.community.upgrade:

Upgrading
---------

#. Begin by updating YUM:

   .. code-block:: bash

      yum update

#. The relevant OpenGeo packages should be included in the upgrade list. If you do not wish to do a full update, cancel the upgrade and install the ``opengeo-suite`` package manually:

   .. code-block:: bash

      yum install opengeo-suite

