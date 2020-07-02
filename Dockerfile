# Image:    s3curitybug/guardedbox
# Build:    docker image build -t s3curitybug/guardedbox .
# Run:      docker run --rm s3curitybug/guardedbox

FROM adoptopenjdk/openjdk14:x86_64-alpine-jre-14.0.1_7@sha256:cdd86f0dcf7d1d4151c37c23b0a22c3369a7c8cbb12b0975ed0c2d8c9ab2963e

LABEL maintainer="s3curitybug@gmail.com"

COPY target/guardedbox*.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar

USER nobody

ENTRYPOINT ["java"]
CMD ["-jar", "/opt/guardedbox/guardedbox.jar", "--spring.config.location=file:/etc/guardedbox/application.properties"]
