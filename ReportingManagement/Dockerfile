
FROM eclipse-temurin:21 as jre-build

WORKDIR /usr/src/reporting-module

COPY target/ReportingManagement-1.0-SNAPSHOT.jar /usr/src/reporting-module/


CMD ["java", "-Xmx64m", "-jar", "/usr/src/reporting-module/ReportingManagement-1.0-SNAPSHOT.jar"]
