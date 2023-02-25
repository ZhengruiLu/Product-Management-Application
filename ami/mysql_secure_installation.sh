#!/bin/bash

# Set root password
echo "Setting root password..."
mysqladmin -u root password 'ChangChang@1'

# Remove anonymous user
echo "Removing anonymous user..."
mysql -u root -p'ChangChang@1' -e "DELETE FROM mysql.user WHERE User='';"
mysql -u root -p'ChangChang@1' -e "FLUSH PRIVILEGES;"

# Disallow root login remotely
echo "Disallowing root login remotely..."
mysql -u root -p'ChangChang@1' -e "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');"
mysql -u root -p'ChangChang@1' -e "FLUSH PRIVILEGES;"

# Remove test database
echo "Removing test database..."
mysql -u root -p'ChangChang@1' -e "DROP DATABASE IF EXISTS test;"
mysql -u root -p'ChangChang@1' -e "DELETE FROM mysql.db WHERE Db='test' OR Db='test\\_%';"
mysql -u root -p'ChangChang@1' -e "FLUSH PRIVILEGES;"

echo "MySQL secure installation completed!"
