#!/bin/bash

set -e

echo "Building the Maven project..."

pushd messaging-utilities-3.4.2
./build.sh
popd

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