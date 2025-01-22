#!/bin/bash

set -e

echo "Building the Maven project..."

# Step 1: Build individual Maven projects
# Ensure that each service's JAR file gets generated
echo "Building messaging-utilities project..."
mvn -f ./messaging-utilities-3.4.2/pom.xml clean package

echo "Installing messaging-utilities JAR as a Maven dependency..."
mvn install:install-file \
    -Dfile=./messaging-utilities-3.4.2/target/messaging-utilities-3.4.2.jar \
    -DgroupId=dk.dtu.hubert \
    -DartifactId=messaging-utilities \
    -Dversion=3.4.2 \
    -Dpackaging=jar

echo "Building DTU-Pay-Server project..."
mvn -f ./DTU-Pay-Server/pom.xml clean package

echo "Building AccountManagement project..."
mvn -f ./AccountManagement/pom.xml clean package

echo "Building TokenManagement project..."
mvn -f ./TokenManagement/pom.xml clean package

echo "Building PaymentManagement project..."
mvn -f ./PaymentManagement/pom.xml clean package

echo "Building ReportingManagement project..."
mvn -f ./ReportingManagement/pom.xml clean package

# Step 2: Build Docker containers
echo "Building and running Docker containers..."
docker compose build
docker compose up -d

# Step 3: Wait for the services to be ready
echo "Waiting for services to initialize..."
sleep 10  # You may need to adjust the sleep duration

# Step 4: Output URLs for Swagger UI access
echo "Services are running. You can check Swagger UI at the following URLs:"
echo "DTU Pay Service: http://localhost:8080/swagger-ui.html"
echo "Account Management Service: http://localhost:8081/swagger-ui.html"
echo "Token Management Service: http://localhost:8082/swagger-ui.html"
echo "Payment Management Service: http://localhost:8083/swagger-ui.html"
echo "Reporting Management Service: http://localhost:8084/swagger-ui.html"

mvn -f ./Client test