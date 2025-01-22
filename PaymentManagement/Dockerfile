FROM eclipse-temurin:21 as jre-build


WORKDIR /usr/src/payment-module

COPY target/PaymentManagement-1.0-SNAPSHOT.jar /usr/src/payment-module/

CMD ["java", "-Xmx64m", "-jar", "/usr/src/payment-module/PaymentManagement-1.0-SNAPSHOT.jar"]
