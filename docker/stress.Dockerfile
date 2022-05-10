FROM ubuntu:21.04
LABEL maintainer="xf.yefei"

RUN apt-get upgrade \
    && apt-get install -y stress stress-ng

ENTRYPOINT [ "stress-ng" ]
