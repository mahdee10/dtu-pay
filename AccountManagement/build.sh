#!/bin/bash
set -e

mvn -f ./../messaging-utilities-3.4.2/pom.xml clean install
mvn -f ./../messaging-utilities-3.4.2/pom.xml clean package

mvn install:install-file \
    -Dfile=./../messaging-utilities-3.4.2/target/messaging-utilities-3.4.2.jar \
    -DgroupId=dk.dtu.hubert \
    -DartifactId=messaging-utilities \
    -Dversion=3.4.2 \
    -Dpackaging=jar

mvn clean install
mvn clean package
docker-compose up
