# webapp
## a. Prerequisites for building and deploying your application locally.
Tools: NodeJS
Database: MySQL
IDE: Visual Studio

## b. Build and Deploy instructions for the web application.

### Part1 NodeJS
npm install
npm i express mysql

### Part2 Database
Start a MySQL instance on port 3306. 

Create a database named userdb. The statement details are:
create database userdb;
use userdb;
create table User(
	id int auto_increment primary key,
    first_name varchar(255),
    last_name varchar(255),
    password varchar(255),
    username varchar(255),
    account_created timestamp default current_timestamp,
    account_updated timestamp default current_timestamp
)

insert into User(first_name, last_name, password, username) 
values("Jane", "Street",
"dGVzdA==", "jane@gmail.com");

alter table user
change account_updated
account_updated timestamp not null
default current_timestamp
on update current_timestamp;

alter table user
add unique(username);

### Part3 Start the application:
node server.js
Play with the server at localhost:3000.