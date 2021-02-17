#!/bin/bash
#

set -e

docker run --rm -it -v "$(pwd)/server:/usr/src/mymaven" -w /usr/src/mymaven maven:3-openjdk-17 mvn package
docker build -t robol/pdfsignatureverifier server
docker build -t robol/pdfsignatureverifier-frontend frontend

echo "You may want to push the images to the cloud by running:"
echo "$ docker push robol/pdfsignatureverifier"
echo "$ docker push robol/pdfsignatureverifier-frontend"
echo ""
echo -n "Shall I run these commands for you? [yn]: "

read ans

if [ "$ans" = "y" ]; then
  docker push robol/pdfsignatureverifier
  docker push robol/pdfsignatureverifier-frontend
fi
