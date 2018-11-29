FROM alpine:3.8
MAINTAINER s3curitybug@gmail.com
RUN apk add --no-cache openjdk8-jre-base=8.181.13-r0
COPY target/guardedbox-1.0.0.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar
USER nobody
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/guardedbox/guardedbox.jar"]
