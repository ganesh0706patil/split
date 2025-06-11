# ---- Stage 1: Build ----
# Use an official Maven image to build the application JAR
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# ---- Stage 2: Run ----
# Use a slim, official JRE image for the final container
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app

# BEST PRACTICE: Use a wildcard to copy the JAR regardless of version number
COPY --from=build /app/target/split-app-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
