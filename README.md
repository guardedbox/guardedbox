# GuardedBox

GuardedBox is an OpenSource online secret manager. It allows users to upload secrets to a centralized
server and retrieve them at anytime and anywhere. It also allows users to share their secrets with other
users.

Secrets are stored encrypted server-side. The encryption is perfomed client-side by javascript. It is 
based on ECC-Curve25519-AES256-GCM asymmetric encryption. The ECC key pair is generated from the user login
credentials during the login process, by means of PBKDF2.

The server knows the public key of every user. Any user can retrieve the public key of any other user and
encrypt a secret for him, such that only that user will be able to decrypt it, using his own private key
generated from his credentials. This is all done client-side by javascript.

The server does not receive the user password during the login process. Instead, a cryptochallenge is
used. When a user wants to perform a login, the server sends him a token. The user must sign it with
his private key and send it back to the server. Again, this is all done client-side by javascript.

# Online Service

GuardedBox is deployed online:

https://www.guardedbox.com

It is a free service for anyone!

# Technical Documentation and On-Premise Deployment

GuardedBox is a Java/Spring-Boot project. See the file pom.xml and the folder src.
The front-end is based on ReactJS. See the front folder.
The used database is MySQL. See the sql folder.

The project is built with the following command at its root directory:

```shell
mvn clean install
```

A jar will be generated in the folder target. Run it with the following command at the project root directory:

```shell
java -jar target/guardedbox-1.0.0.jar --spring.config.location=file:./properties-example/application.properties
```

It requires a MySQL instance with the schema described in the file sql/guardedbox.sql.

It also requires an external properties file (the application.properties in the previous command). An
example of properties file can be found at the folder properties-example.

The project is also dockerized. The image is built with the following command at the project root directory:

```shell
docker image build -t s3curitybug/guardedbox -f docker/guardedbox/Dockerfile .
```

The container can be run locally with the following command at the project root directory:

```shell
docker-compose -f docker/docker-compose.yml up
```

Make sure the secrets paths (which point to the properties file) are right in the docker-compose.yml file.

The image is available at Docker Hub:

- https://hub.docker.com/r/s3curitybug/guardedbox/

It still requires a MySQL instance and a properties file.
