# Project - *Product Management Application* - Network Structures and Cloud Computing

**Product Management Application** is a secure application for managing user account and product info. A Web Application using Sprint Boot that meets Cloud-Native Web Application Requirements. It implemented RESTful APIs that return with proper HTTP status codes based on user stories.

Submitted by: **Zhengrui Lu**

Time spent: **40** hours spent in total

## User Stories

The following **required** functionality is completed:

### API
* [X] **RESTful API** Endpoints To Be Implemented: https://app.swaggerhub.com/apis-docs/csye6225-webapp/cloud-native-webapp/spring2023-a5
* [X] All API request/response payloads should be in **JSON**.
* [X] All API calls to return with a proper **HTTP status code**.
* [X] Users should be **authenticated & authorized** where applicable.

### Bootstrap Database
* [X] The application is expected to automatically bootstrap the database at startup.
* [X] Bootstrapping creates the schema, tables, indexes, sequences, etc. Or updates them if their definition has changed.

### Infrastructure as Code with Terraform and Packer
* [X] Deployed Application by launching the AMI using Amazon Linux 2 via **Packer**.
* [X] Setup autorun using **Systemd**.
* [X] Used **Terraform** configuration file to create all AWS resources needed. See [Repository: aws-infra](https://github.com/ZhengruiLu/aws-infra)

### CI/CD with Github Action
* [X] Pull Request Raised Workflow.
    * [X] Add a GitHub Action workflow to run the application unit tests for each pull request raised.
    * [X] A pull request can only be merged if the workflow executes successfully.
* [X] Pull Request Merged Workflow.
    * [X] Add another GitHub actions workflow and configure it to be triggered when a pull request is merged. This workflow should do the following:
        - a. Run the unit test.
        - b. Validate Packer Template
        - c. Build Application Artifact(s)
        - d. Build AMI
            - i. Upgrade OS packages
            - ii. Install dependencies (python, node.js, etc.)
            - iii. Install application dependencies (pip install for Python)
            - iv. Set up the application by copying the application artifacts and the configuration files.
            - v. Configure the application to start automatically when VM is launched.
        - e. Create a new Launch Template version with the latest AMI ID for the autoscaling group. The autoscaling group should be configured to use the latest version of the Launch Template.
        - f. Issue command to the auto-scale group to do an instance refresh.

## Notes
Describe any challenges encountered while building the app.

### Wep Application Dev
* [X] Learn how to use the Spring Boot Framework to build a project.
* [X] How to add authenticated and authorized functionality.
* [X] Differentiate between HTTP status codes, such as 401 and 403.

### Cloud
* [X] Understand the functions and application methods of AWS related services.

## Build and Deploy Instructions
### a. Prerequisites for building and deploying your application locally.
- Framework: SpringBoot
- Database: MySQL/MariaDB
- Infrastructure as Code: Terraform, Packer
- Autorun: Systemd
- Cloud: AWS
- CI/CD: Github Action
- IDE: Intellij
- Test Endpoints: Postman

### b. Build and Deploy instructions for the web application.
#### Part1 Maven Project
After clone the repository to local, open the directory ProductManager,
find pom.xml, right click it and choose "Add it as a Maven project",
then right click it again, choose "Maven" - "Reload Project".

#### Part2 Bootstrapping Database
Find the directory "resources", find the file application.yml. Find the database part,
and fill in with your MySQL/MariaDB username and password.
```
spring:
datasource:
driver-class-name: com.mysql.cj.jdbc.Driver
url: jdbc:mysql://127.0.0.1:3306/usertestdb
username: add your username
password: add your password
```

#### Part3 Start the application
Find the file ProductManagerApplication in ProductManager - src - main - java - com.csye6225.productmanager,
Click run.

#### Part4 Test with Postman
Authorization - Choose "Basic Auth"
Username: same with created user's username - email
Password: somepassword

Please change params according to your setting.
##### Sample URL and its components
- URL 	http://localhost:8080/v1/product?name=book&description=test
- scheme	    http
- hostname      localhost
- port	        8080
- origin        http://localhost:8080
- path	        /v1/product
- query	        ?name=book&description=test

##### Sample URLs
- POST: http://localhost:8080/v1/product?name=book&description=test description&sku=XYZ12345&manufacturer=test manufacturer&quantity=1
- GET: http://localhost:8080/v1/product/1
- PUT: http://localhost:8080/v1/product/1?quantity=12
- PATCH: http://localhost:8080/v1/product/1?quantity=10
- DELETE: http://localhost:8080/v1/product/1

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