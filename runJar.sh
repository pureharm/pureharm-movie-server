# usage:
# ./runJar.sh start  — starts the server from a bundled jar, if the jar doesn't exist it creates it
# ./runJar.sh clean  — starts the server from a newly created jar, deletes old jar
# ./runJar.sh        — default, same as start

#Basically, it just run two commands:
#sbt mkJar;
#java -jar server/target/scala-2.12/pure-movie-server.jar

CMD_CLEAN='clean'
CMD_START='start'

#the name of the executable jar that is created using `sbt mkJar`
JAR_NAME=server/target/scala-2.12/pure-movie-server.jar

warning() {
  echo ""
  echo "*********"
  echo "[WARNING]: $1";
  echo "*********"
  echo ""
}

info() {
  echo ""
  echo "[INFO] $1"
  echo ""
}

if (( $# == 0 ));
then
  warning "-- no command line arguments specified. Defaulting to command: $CMD_START"
  user_cmd="$CMD_START"
else
  user_cmd="$1"
fi

if [ "$user_cmd" == "$CMD_START" ]
then
  if [ -f $JAR_NAME ]
  then
    info "jar already exists."
  else
    info "jar does not exist. creating using: 'sbt mkJar'"
    sbt mkJar
  fi #jar_name

  info "running: 'java -jar $JAR_NAME'"
  java -jar $JAR_NAME

elif [ "$user_cmd" == "$CMD_CLEAN" ]
then
  info "clean + recreating jar: 'sbt mkJar'"
  sbt mkJar

  info "running: 'java -jar $JAR_NAME'"
  java -jar $JAR_NAME
fi


