---
layout: docs
title: Getting Started
---

# Getting Started

### 1. grabing the code

install `git`  by following:
[https://git-scm.com/book/en/v2/Getting-Started-Installing-Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

Head over to the github page [https://github.com/busymachines/pure-movie-server](https://github.com/busymachines/pure-movie-server) and clone the repository:
`git clone git@github.com:busymachines/pure-movie-server.git` in a folder of your choosing.

### 2. Runtime environment Java JDK8

The safest is to just ensure that you have the Java 8 JDK installed. Technically it should run on newer versions of the java virtual machine, but it wasn't properly tested.
Download [jdk8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### 3. Build tool
install SBT [https://www.scala-sbt.org/download.html](https://www.scala-sbt.org/download.html)

All large software projects kinda require some special build tool to be able to manage the complexity of wiring together all modules amongst themselves, and grabbing all external dependencies and whatnot. Additionally, they provide ways of automating testing, and packaging and deploying the application.

The "default" option in the Scala ecosystem is SBT (Scala build tool), although there are other emerging alternatives.

### 4. IDE support IntelliJ

install IntelliJ [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)

One of the easiest tools for editing and navigating Scala code is IntelliJ. While it has its bugs, it is rather user friendly.

See this [tutorial]() on how to import the `pure-movie-server` once you cloned it from github (see first section).

### 5. Run a PostgresSQL database server

install Docker [https://www.docker.com/get-started](https://www.docker.com/get-started)

After installing docker, you can simply start a PostgreSQL server by running the script `./docker-postgresql.sh` from the root project folder. Once you start the server, it will connect to this database.