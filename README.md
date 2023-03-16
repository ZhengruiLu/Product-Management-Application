# webapp
## a. Prerequisites for building and deploying your application locally.
Tools: SpringBoot
Database: MySQL
IDE: Intellij
Test: Postman
Autorun: Systemd


## b. Build and Deploy instructions for the web application.

### Part1 Maven Project
After clone the repository to local, open the directory ProductManager, 
find pom.xml, right click it and choose "Add it as a Maven project", 
then right click it again, choose "Maven" - "Reload Project".

### Part2 Bootstrapping Database
Find the directory "resources", find the file application.yml. Find the database part,
and fill in with your mysql username and password.
...

spring:
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://127.0.0.1:3306/usertestdb
username: add your username
password: add your password

### Part3 Start the application
Find the file ProductManagerApplication in ProductManager - src - main - java - com.csye6225.productmanager,
Click run.

### Part4 Test with Postman
Authorization - Choose "Basic Auth"
Username: admin
Password: password

Please change params as you want.

POST: http://localhost:8080/v1/product?name=book&description=test description&sku=XYZ12345&manufacturer=test manufacturer&quantity=1
GET: http://localhost:8080/v1/product/1
PUT: http://localhost:8080/v1/product/1?quantity=12
PATCH: http://localhost:8080/v1/product/1?quantity=10
DELETE: http://localhost:8080/v1/product/1
