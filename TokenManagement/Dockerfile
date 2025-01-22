
FROM eclipse-temurin:21 as jre-build


WORKDIR /usr/src/token-module

COPY target/TokenManagement-1.0-SNAPSHOT.jar /usr/src/token-module/


CMD ["java", "-Xmx64m", "-jar", "/usr/src/token-module/TokenManagement-1.0-SNAPSHOT.jar"]
