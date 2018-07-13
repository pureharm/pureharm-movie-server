CONTAINER_NAME=postgres_pms

if [ "$(docker ps -aq -f name=$CONTAINER_NAME)" ]; then
	if [ ! "$(docker ps -aq -f name=$CONTAINER_NAME -f status=exited)" ]; then
		echo "Stoping postgres container"
		docker stop $CONTAINER_NAME
	fi
	echo "Starting postgres container"
	docker start $CONTAINER_NAME
else
	echo "Creating postgres container"
	docker run -d \
		--name $CONTAINER_NAME \
		-p 5432:5432 \
		-e POSTGRES_DB=mymoviedatabase \
		-e POSTGRES_USER=busyuser \
		-e POSTGRES_PASSWORD=qwerty \
		postgres:10.4
fi