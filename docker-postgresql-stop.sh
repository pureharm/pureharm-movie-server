#!/usr/bin/env bash

CONTAINER_NAME=postgres_phms

docker stop $(docker ps --format "{{.ID}}" --filter "name=$CONTAINER_NAME")