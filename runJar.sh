# usage:
# ./runJar.sh start  — starts the server from a bundled jar, if the jar doesn't exist it creates it
# ./runJar.sh clean  — starts the server from a newly created jar, deletes old jar
# ./runJar.sh        — default, same as start

#Basically, it just run two commands:
#sbt mkJar;
#./modules/apps/server/target/universal/stage/bin/phms-app-server

CMD_CLEAN='clean'
CMD_START='start'

#the name of the executable jar that is created using `sbt mkJar`
SBT_ARGS='mkJar'
SCRIPT_NAME='modules/apps/server/target/universal/stage/bin/phms-app-server'

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
  if [ -f $SCRIPT_NAME ]
  then
    info "executable already exists."
  else
    info "executable does not exist. creating using: 'sbt $SBT_ARGS'"
    sbt $SBT_ARGS
  fi #SCRIPT_NAME

  info "running: './$SCRIPT_NAME'"
  ./$SCRIPT_NAME

elif [ "$user_cmd" == "$CMD_CLEAN" ]
then
  info "clean + recreating jar: 'sbt mkJar'"
  sbt mkJar

  info "running: './$SCRIPT_NAME'"
  ./$SCRIPT_NAME
fi


