FROM alpine:3.10.3
MAINTAINER s3curitybug@gmail.com
RUN apk add --update --no-cache openjdk11-jre=11.0.4_p4-r1
COPY target/guardedbox*.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar
USER nobody
ENTRYPOINT ["java", "-jar", "/opt/guardedbox/guardedbox.jar"]
