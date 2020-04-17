# GuardedBox

GuardedBox is an open-source online client-side manager for secure storage and secrets sharing. 

It allows users to upload secrets to a centralized server and retrieve them at anytime and from anywhere. 
It also allows users to share their secrets with other users, individually or via groups.

Secrets are stored encrypted server-side. The encryption is performed client-side by JavaScript code. It is 
based on ECC-Curve25519 asymmetric encryption and AES256-GCM symmetric encryption. The ECC key pair is 
generated from the user login credentials during the registration and login processes, by means of PBKDF2.

The server knows the public key of every user. Any user can retrieve the public key of any other user and
encrypt a secret for her, in a way that only that user will be able to decrypt it, using his own private key
generated from his credentials. This is all done client-side by JavaScript code, minimizing the trust on the server,
and using End to End (E2E) encryption between users.

The server does not receive the user password during the login process. Instead, a crypto-challenge is
involved using digital signatures based on ECC-EDDSA with ED25519. When a user wants to perform a login, 
the server sends him a challenge. The user must sign it with his private key and send it back to the server. 
Again, this is all done client-side by JavaScript code.

# Online Service

GuardedBox is deployed online. The official details, notification and communication channels, version information (and changelog) and documentation, as well as the reference to the online service, are available at:

- https://www.guardedbox.es (Spanish)

- https://www.guardedbox.eu (English - soon)

It is a free service for anyone: individuals, companies and organizations!

# Technical Documentation and Local Deployment

GuardedBox is a JavaScript and Java/Spring-Boot project:
- The back-end is based on Java/Spring-Boot. See the "pom.xml" file and the "java" folder (inside "src/main").
- The front-end is based on JavaScript using ReactJS. See the "front" folder (inside "src/main").
- The database is MySQL. See the "sql" folder (inside "src/main").

The project can be built via Maven with the following command from its root directory:

```shell
mvn clean install
```

A JAR file (.jar) will be generated in the "target" folder. 

The project can be run with the following command from the project root directory:

```shell
java -jar target/guardedbox-1.0.0.jar --spring.config.location=file:./config-example/application.properties
```

It requires a MySQL database instance with the schema described in the file "sql/guardedbox.sql" (inside "src/main").

It also requires an external properties file (the "application.properties" reference in the previous command). An
example of a properties file can be found in the "config-example" folder, plus a server digital certificate for HTTPS.

The project is also dockerized. The image is built during the Maven life cycle. The container can be run locally
with the following command from the project root directory:

```shell
docker-compose up
```

Make sure the secrets paths (which point to the properties file) are right in the "docker-compose.yml" file.

The image is available at Docker Hub:

- https://hub.docker.com/r/s3curitybug/guardedbox/

It still requires, as detailed above, a MySQL database instance and a properties file, plus a server digital certificate for HTTPS.

# Contact Details

The GuardedBox project contact details and communication channels are available [here!](https://guardedbox.es/index.html#contacto)
