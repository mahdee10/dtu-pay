FROM eclipse-temurin:21 as jre-build
WORKDIR /usr/src
COPY target/ /usr/src/quarkus-app
CMD ["java", "-Xmx64m", "-jar", "quarkus-app/ReportingManagement-1.0-SNAPSHOT.jar"]