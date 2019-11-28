FROM alpine:3.10
MAINTAINER s3curitybug@gmail.com
COPY target/guardedbox-1.0.0.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar \
	&& apk add --update --no-cache openjdk11-jre=11.0.4_p4-r1
USER nobody
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/guardedbox/guardedbox.jar"]
