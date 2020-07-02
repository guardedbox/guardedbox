# Image:            s3curitybug/guardedbox-build
# Build:            docker image build -t s3curitybug/guardedbox-build -f build.Dockerfile .
# Run (Linux):      docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v $(pwd):/guardedbox -v $M2_REPO:/root/.m2/repository s3curitybug/guardedbox-build
# Run (Windows):    docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v %cd%:/guardedbox -v %M2_REPO%:/root/.m2/repository s3curitybug/guardedbox-build

FROM adoptopenjdk/openjdk14:x86_64-alpine-jdk-14.0.1_7@sha256:c50307183937904b43dbdd7946b308859ea6b56d2005ad2ca0537414108b6a2b

LABEL maintainer="s3curitybug@gmail.com"

RUN apk add --update --no-cache maven npm docker openrc && \
    rc-update add docker boot

WORKDIR /guardedbox

ENTRYPOINT ["mvn", "clean", "install"]
