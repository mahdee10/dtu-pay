FROM eclipse-temurin:21 as jre-build
WORKDIR /usr/src
COPY target/quarkus-app /usr/src/payment-module
CMD ["java", "-Xmx64m", "-jar", "payment-module/AccountManagement-1.0-SNAPSHOT.jar"]