FROM adoptopenjdk/openjdk13:alpine-jre@sha256:92bdf255a0ebc7ab51d37bf04a0d15b1d4fed7e56395e71b1f2096ba5eb30293
LABEL maintainer="s3curitybug@gmail.com"
COPY target/guardedbox*.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar
USER nobody
ENTRYPOINT ["java"]
CMD ["-jar", "/opt/guardedbox/guardedbox.jar", "--spring.config.location=file:/etc/guardedbox/application.properties"]
