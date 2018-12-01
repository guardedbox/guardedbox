# GuardedBox

GuardedBox is an OpenSource online secret manager. It allows users to upload secrets to a centralized
server and retrieve them at anytime and anywhere. It also allows users to share their secrets with other
users.

Secrets are stored encrypted server-side. The encryption is perfomed client-side by javascript. It is 
based on RSA2048-AES256-CBC asymmetric encryption. The RSA key pair is generated from the user login
credentials during the login process. The AES keys are generated randomly for each encryption.

The server knows the public key of every user. Any user can retrieve the public key of any other user and
encrypt a secret for him, such that only that user will be able to decrypt it, using his own private key
generated from his credentials. This is all done client-side by javascript.

The server does not receive the user password during the login process. Instead, a cryptochallenge is
used. When a user wants to perform a login, the server sends him a token. The user must sign it with his
RSA private key and send it back to the server. Again, this is all done client-side by javascript.

# Online Service

GuardedBox is deployed online:

https://guardedbox.com

It is a free service for anyone!

# Technical Documentation and On-Premise Deployment

GuardedBox is a Java/Spring-Boot project. See the file pom.xml and the folder src.
The front-end is based on ReactJS. See the front folder.
The used database is MySQL. See the sql folder.

The project is built with the following command at its root directory:

```shell
mvn clean install
```

A jar will be generated in the folder target. Run it with the following command:

```shell
java -jar guardedbox.jar --spring.config.location=file:application.properties
```

It requires a MySQL instance with the schema described in the file sql/guardedbox.sql. It also requires
a external properties file (the application.properties in the previous command). It accepts any
Spring-Boot property, included the MySQL connection URL.

Spring-Boot properties: https://docs.spring.io/spring-boot/docs/1.5.x/reference/html/common-application-properties.html

A example of properties files can be found at the folder config.

The project is also dockerized. The main image is built with the following command at the project root
directory:

```shell
docker image build -t s3curitybug/guardedbox -f docker/guardedbox/Dockerfile .
```

There is also a secondary image, consisting on an Nginx used to redirect traffic from http to https. It
is built with the following command:

```shell
docker image build -t s3curitybug/guardedbox-nginx -f docker/guardedbox-nginx/Dockerfile .
```

The Nginx configuration file is in the folder nginx.

The containers can be run locally with the following command at the project root directory:

```shell
docker-compose -f docker/docker-compose.yml up
```

Both images are available at Docker Hub:

https://hub.docker.com/r/s3curitybug/guardedbox/
https://hub.docker.com/r/s3curitybug/guardedbox-nginx/

They still require a MySQL instance and a properties file that may be introduced in a external volume.
