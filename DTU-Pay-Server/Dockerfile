# Use a JDK base image to build and run the app
FROM eclipse-temurin:21 as jre-build

# Set working directory inside the container
WORKDIR /usr/src

# Copy the quarkus-app folder from your build directory into the container
COPY target/quarkus-app /usr/src/quarkus-app

# Set the entrypoint to run the Quarkus application
CMD ["java", "-Xmx64m", "-jar", "/usr/src/quarkus-app/quarkus-run.jar"]
