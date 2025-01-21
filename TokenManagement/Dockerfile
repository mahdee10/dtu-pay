FROM eclipse-temurin:21 as jre-build
WORKDIR /usr/src
COPY target/quarkus-app /usr/src/token-module
CMD ["java", "-Xmx64m", "-jar", "token-module/TokenManagement-1.0-SNAPSHOT.jar"]