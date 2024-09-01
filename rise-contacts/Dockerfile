FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the Maven wrapper and POM file
COPY mvnw pom.xml ./

# Copy the .mvn directory
COPY .mvn .mvn

# Give executable permission to the Maven wrapper script
RUN chmod +x mvnw

# Convert line endings for Maven wrapper script
RUN sed -i 's/\r$//' mvnw

# Copy the application source code
COPY src ./src

# Copy the contacts.json file to the /app directory
COPY src/main/resources/contacts.json ./contacts.json

# Package the application using Maven
RUN ./mvnw package -DskipTests

# Set the entry point
ENTRYPOINT ["java", "-jar", "./target/rise-contacts-0.0.1-SNAPSHOT.jar"]
