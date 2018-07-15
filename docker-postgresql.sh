#!/usr/bin/env bash
# Script that setups and runs a postgres 10.4 database inside a docker container.
# You can run this script via terminal using `sh docker-postgresql.sh`

# Running the script for the first time pulls a postgres image from docker repository.
# If a container with the same name already exists, running this script will start/restart the container.
# Postgres default configuration is specified by the parameters below.
# Docker is required in order for this script to work !

# parameters #
CONTAINER_NAME=postgres_pms # Name of the docker container used to run postgres
DB_PORT=5432
DB_NAME=mymoviedatabase
DB_USER=busyuser
DB_PASS=qwerty

# actual script #
if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
	if [ ! "$(docker ps -aq -f name=$CONTAINER_NAME -f status=exited)" ]; then
		echo "Stopping postgres container"
		docker stop $CONTAINER_NAME
	fi
	echo "Starting postgres container"
	docker start $CONTAINER_NAME
else
	echo "Creating & starting postgres container"
	docker run -d \
		--name $CONTAINER_NAME \
		-p $DB_PORT:$DB_PORT \
		-e POSTGRES_DB=$DB_NAME \
		-e POSTGRES_USER=$DB_USER \
		-e POSTGRES_PASSWORD=$DB_PASS \
		postgres:10.4
fi