#!/usr/bin/env bash

CONTAINER_NAME=mailhog_phms

docker stop $(docker ps --format "{{.ID}}" --filter "name=$CONTAINER_NAME")