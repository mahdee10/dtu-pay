FROM eclipse-temurin:21 as jre-build
WORKDIR /usr/src
COPY target/ /usr/src/payment-module
CMD ["java", "-Xmx64m", "-jar", "payment-modules/PaymentManagement-1.0-SNAPSHOT.jarr"]