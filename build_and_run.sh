#!/bin/bash

set -e

echo "Building the Maven project..."

mvn -f ./messaging-utilities-3.4.2/pom.xml clean package

mvn install:install-file \
    -Dfile=./messaging-utilities-3.4.2/target/messaging-utilities-3.4.2.jar \
    -DgroupId=dk.dtu.hubert \
    -DartifactId=messaging-utilities \
    -Dversion=3.4.2 \
    -Dpackaging=jar

mvn -f ./DTU-Pay-Server/pom.xml clean package
mvn -f ./AccountManagement/pom.xml clean package
mvn -f ./TokenManagement/pom.xml clean package
mvn -f ./PaymentManagement/pom.xml clean package
mvn -f ./ReportingManagement/pom.xml clean package

echo "Building and running Docker containers..."
docker compose build
docker compose up -d

sleep 10

mvn -f ./Client test