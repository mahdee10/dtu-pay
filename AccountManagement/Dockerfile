FROM eclipse-temurin:21 as jre-build
COPY target/ /usr/src/
COPY target/AccountManagement-1.0-SNAPSHOT.jar /usr/src/
WORKDIR /usr/src
CMD ["java", "-Xmx64m", "-jar", "AccountManagement-1.0-SNAPSHOT.jar"]