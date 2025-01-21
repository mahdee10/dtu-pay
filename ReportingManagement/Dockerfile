FROM eclipse-temurin:21 as jre-build
WORKDIR /usr/src
COPY target/quarkus-app /usr/src/reporting-module
CMD ["java", "-Xmx64m", "-jar", "reporting-module/ReportingManagement-1.0-SNAPSHOT.jar"]