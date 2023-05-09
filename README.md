# Project - *Product Management Application* - project of Network Structures and Cloud Computing

**Product Management Application** is a Web Application using Sprint Boot that meets Cloud-Native Web Application Requirements. It implemented RESTful APIs that return with proper HTTP status codes based on user stories. 

Submitted by: **Zhengrui Lu**

Time spent: **80** hours spent in total

## User Stories

The following **required** functionality is completed:

### API Requirements
* [X] RESTful API Endpoints To Be Implemented: https://app.swaggerhub.com/apis-docs/csye6225-webapp/cloud-native-webapp/spring2023-a5
* [X] All API request/response payloads should be in JSON.
* [X] As a user, I expect all API calls to return with a proper HTTP status code.
* [X] Users should be authenticated & authorized where applicable.
* [X] Users can upload images to the products they have created.

### Bootstrap Database Requirements
* [X] The application is expected to automatically bootstrap the database at startup.
* [X] Bootstrapping creates the schema, tables, indexes, sequences, etc., or updates them if their definition has changed.

### S3 bucket Requirements
* [X] You must add support for popular file types such as jpeg, jpg, png, etc.
* [X] Each image must be stored in an S3 bucket.
* [X] Object metadata must be stored in the database.
* [X] Users can (hard) delete images they have uploaded.
* [X] The image must be deleted from the S3 bucket.
* [X] Users cannot update their images. 
* [X] Users can delete only their own images from the products they have created.
* [X] Users should not be able to delete images uploaded by other users or from products created by other users.
* [X] Multiple users can upload images with the same name. You must ensure the partitioning user's images in the object storage bucket.
* [X] S3 credentials should not be hardcoded anywhere and the application must be able to access S3 using the IAM role attached to the EC2 instance.



## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='walkthrough.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

* [X] Remember the process of project development
* [X] Remember **git** process
* [ ] Adding the **Editing** Feature
* [ ] Improve the **UI / UX** of apps including icons, styling, color, **spacing** 

## License

    Copyright [yyyy] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

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

### Part5 Command to Import SSL Certificate
aws acm import-certificate --certificate fileb://demo_zltech_me.crt --certificate-chain fileb://demo.zltech.me.ca-bundle --private-key fileb://demo.zltech.me.key

Please change params as you want.

POST: http://localhost:8080/v1/product?name=book&description=test description&sku=XYZ12345&manufacturer=test manufacturer&quantity=1
GET: http://localhost:8080/v1/product/1
PUT: http://localhost:8080/v1/product/1?quantity=12
PATCH: http://localhost:8080/v1/product/1?quantity=10
DELETE: http://localhost:8080/v1/product/1
