# Image:            s3curitybug/guardedbox-build
# Build:            docker image build -t s3curitybug/guardedbox-build -f build.Dockerfile .
# Run (Linux):      docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/guardedbox -v $M2_REPO:/root/.m2/repository s3curitybug/guardedbox-build
# Run (Windows):    docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v %cd%:/guardedbox -v %M2_REPO%:/root/.m2/repository s3curitybug/guardedbox-build
# Versions:
#   Alpine: 3.12.0
#   JDK:    14.0.2+12
#   Maven:  3.6.3
#   Node:   12.18.3
#   NPM:    6.14.6
#   Docker: 19.03.12

FROM adoptopenjdk/openjdk14:x86_64-alpine-jdk-14.0.2_12@sha256:2d486188ce342f1351e9e050121440fd67564e179ce1e872b68147e05321c751

LABEL maintainer="s3curitybug@gmail.com"

RUN apk add --update --no-cache maven npm docker openrc && \
    rc-update add docker boot

WORKDIR /guardedbox

ENTRYPOINT ["mvn", "clean", "install"]
