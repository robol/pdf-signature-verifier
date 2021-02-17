#!/bin/bash
#

set -e

docker run --rm -it -v "$(pwd)/server:/usr/src/mymaven" -w /usr/src/mymaven maven:3-openjdk-17 mvn package
docker build -t robol/pdfsignatureverifier server
docker build -t robol/pdfsignatureverifier-frontend frontend
