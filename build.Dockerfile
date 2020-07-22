# Image:            s3curitybug/guardedbox-build
# Build:            docker image build -t s3curitybug/guardedbox-build -f build.Dockerfile .
# Run (Linux):      docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/guardedbox -v $M2_REPO:/root/.m2/repository s3curitybug/guardedbox-build
# Run (Windows):    docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v %cd%:/guardedbox -v %M2_REPO%:/root/.m2/repository s3curitybug/guardedbox-build

FROM adoptopenjdk/openjdk14:x86_64-alpine-jdk-14.0.2_12@sha256:679597daf90eda6a0c4da75ab3ea872984c22b363ddcdec5e13ff73cc8ca6465

LABEL maintainer="s3curitybug@gmail.com"

RUN apk add --update --no-cache maven npm docker openrc && \
    rc-update add docker boot

WORKDIR /guardedbox

ENTRYPOINT ["mvn", "clean", "install"]
