# OpenGeo Suite Vagrant Boxes
## A set of [Vagrant](http://vagrantup.com) "boxes" for auto-deploying OpenGeo Suite

### How to use these
- Install [VirtualBox](https://www.virtualbox.org).
- Install [Vagrant](http://vagrantup.com).
- To start a box, run `vagrant up` in the directory of the box you want to run.
  - This will install OpenGeo Suite from the test repo.
  - To install from another repo, set the REPO environment variable to the name of the repo (ie `REPO=stable vagrant up`).
- You can test an upgrade by first running `REPO=stable vagrant up` to install the current release, then `REPO=test vagrant provision` to re-run the setup script with the test repo.
- You can shutdown a box by running `vagrant halt`. This will keep the box's state for the next boot. If you need to bring the box back up without reinstalling the suite, use `vagrant up --no-provision`.
- You can reset the box to its initial state by running `vagrant destroy`.

### How can I access services running on these boxes?
There are port forwards that allow you to access tomcat from the host machine on port 8888 (ie http://localhost:8888). PostgreSQL is passed right through to the host machine on the default port (5432). You can ssh into the box by running `vagrant ssh` in the box's directory.