
````shell script
# launch server
docker run --name mysql --rm -p 3306:3306 -e MYSQL_USER=spring -e MYSQL_PASSWORD=book -e MYSQL_ALLOW_EMPTY_PASSWORD=example -e MYSQL_DATABASE=springbook mysql:5 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
````

````sql
CREATE TABLE users(
id varchar(10) primary key,
name varchar(20) not null,
password varchar(10) not null,
level tinyint not null,
login int not null,
recommend int not null 
)
````