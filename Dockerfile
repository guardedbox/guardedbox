# Image:    s3curitybug/guardedbox
# Build:    docker image build -t s3curitybug/guardedbox .
# Run:      docker run --rm s3curitybug/guardedbox
# Versions:
#   Alpine: 3.12.0
#   JRE:    14.0.2+12

FROM adoptopenjdk/openjdk14:x86_64-alpine-jre-14.0.2_12@sha256:512fabfb185e2e3fa12072cd5b181200239896a69e49b28a145290d423a4c8a6

LABEL maintainer="s3curitybug@gmail.com"

COPY target/guardedbox*.jar /opt/guardedbox/guardedbox.jar
RUN chmod 444 /opt/guardedbox/guardedbox.jar

USER nobody

ENTRYPOINT ["java"]
CMD ["-jar", "/opt/guardedbox/guardedbox.jar", "--spring.config.location=file:/etc/guardedbox/application.properties"]
