# Image:    s3curitybug/guardedbox
# Build:    docker image build -t s3curitybug/guardedbox .
# Run:      docker run --rm s3curitybug/guardedbox

FROM adoptopenjdk/openjdk14:x86_64-alpine-jre-14.0.2_12@sha256:19edfd215b79af35a043e3b9bca1d8724f5f0e763a3c3ea7e797e5245c3b9d4a

LABEL maintainer="s3curitybug@gmail.com"

COPY target/guardedbox*.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar

USER nobody

ENTRYPOINT ["java"]
CMD ["-jar", "/opt/guardedbox/guardedbox.jar", "--spring.config.location=file:/etc/guardedbox/application.properties"]
