#!/usr/bin/env bash
# Script that setups and runs a SMTP mailhog server + UI in the container.

# Running the script for the first time pulls a postgres image from docker repository.
# If a container with the same name already exists, running this script will start/restart the container.

MAILHOG_VERSION=latest       # see https://hub.docker.com/r/mailhog/mailhog/
CONTAINER_NAME=mailhog_phms  # Name of the docker container
SMTP_EXPOSED_PORT=10125      # this is the port on the host machine; most likely you want to change this one.
SMTP_INTERNAL_PORT=1025      # this is the default port on which mailhog starts on within the container.
UI_EXPOSED_PORT=10126
UI_INTERNAL_PORT=8025

DB_NAME=mymoviedatabase
DB_USER=busyuser
DB_PASS=qwerty

# actual script #
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
	if [ ! "$(docker ps -aq -f name=$CONTAINER_NAME -f status=exited)" ]; then
		echo "Stopping mailhog container"
		docker stop $CONTAINER_NAME
	fi
	echo "Starting mailhog container"
	docker start $CONTAINER_NAME
else
	echo "Creating & starting mailhog container"
	docker run -d \
		--name $CONTAINER_NAME \
		-p $SMTP_EXPOSED_PORT:$SMTP_INTERNAL_PORT \
		-p $UI_EXPOSED_PORT:$UI_INTERNAL_PORT \
		mailhog/mailhog:$MAILHOG_VERSION
fi
