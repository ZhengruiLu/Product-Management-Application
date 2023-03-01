#!/bin/bash
set -euxo pipefail
sudo yum update -y
yes | sudo yum install java-1.8.0-openjdk
yes | sudo yum install maven
sudo yum install -y mariadb-server
sudo systemctl start mariadb
sudo systemctl enable mariadb
echo $'\nY\nChangChang@1\nChangChang@1\nY\nY\nY\nY\n' | sudo mysql_secure_installation
sudo mysql -u root -pChangChang@1 -e 'CREATE DATABASE usertestdb;'
sudo yum clean all
sudo mkdir /opt/deployment
sudo mkdir /var/log/apps
sudo chown -R $USER:$USER /opt/deployment
sudo chown -R $USER:$USER /var/log/apps