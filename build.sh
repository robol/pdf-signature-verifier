#!/bin/bash
#

set -e

sudo docker run --rm -it -v "$(pwd)/server:/usr/src/mymaven" -w /usr/src/mymaven maven:3-openjdk-17 mvn package
sudo docker build -t robol/pdfsignatureverifier server

sudo docker build -t robol/pdfsignatureverifier-frontend frontend
