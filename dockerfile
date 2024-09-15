# Use a base image with JDK installed
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the host to the container
COPY target/moneytransfer-0.0.1-SNAPSHOT.jar /app/money-transfer-service.jar

# Expose the port on which the app runs
EXPOSE 8989

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "money-transfer-service.jar"]
