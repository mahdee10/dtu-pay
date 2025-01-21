FROM eclipse-temurin:21 as jre-build
WORKDIR /usr/src
COPY target/ /usr/src/account-module
CMD ["java", "-Xmx64m", "-jar", "account-module/AccountManagement-1.0-SNAPSHOT.jar"]