FROM maven:3.9.5-amazoncorretto-17-al2023 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY target ./target

# Build a release artifact.
#RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","app/target/usercenter-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]