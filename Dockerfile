# Use a Maven image to build the application
FROM maven:3.8.6-jdk-11 as build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src/ /app/src/
RUN mvn clean package -DskipTests

# Use a lightweight OpenJDK image to run the application
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built jar file from the build container (previous step) to the runtime container
COPY --from=build /app/target/FxDealsApplication-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]