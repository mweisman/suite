class opengeosuite ($repo='stable') {
  # Build and install repo conncetion files
  case $operatingsystem {
    ubuntu: {
      if $repo == 'stable' {
        $install_repo = 'ubuntu'
      }
      file { 'opengeo.list':
        path     => '/etc/apt/sources.list.d/opengeo.list',
        ensure   => present, 
        content  => template('opengeosuite/OpenGeo.list.erb')
      }
      exec { 'install_key':
        command   => 'wget -qO- http://apt.opengeo.org/gpg.key | apt-key add -',
        path      => '/usr/bin'
      }
      exec { 'update_apt':
        command  => 'apt-get update',
        path     => '/usr/bin:/bin',
        require  => File['opengeo.list']
      }
    }
    centos, redhat: {
      # Set repo to either centos or $repo/centos
      if $repo == 'stable' {
        $install_repo = 'centos'
      } else {
        $install_repo = "${repo}/centos"
      }

      # Set arch and EL version
      if $operatingsystemrelease < 6 {
        $el_ver = '5'
        if $architecture == 'x86_64' {
          $arch = $architecture
        } else {
          $arch = 'i386'
        }
      } else {
        $el_ver = '6'
        if $architecture == 'x86_64' {
          $arch = $architecture
        } else {
          $arch = 'i686'
        }
      }

      file { 'OpenGeo.repo':
        path     => '/etc/yum.repos.d/OpenGeo.repo',
        content  => template('opengeosuite/OpenGeo.repo.erb')
      }
    }
  }
  
  # Install the suite
  package { 'opengeo-suite':
    ensure  => installed
  }
}
