FROM maven:3.9.0-eclipse-temurin-17 AS build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar", "--spring.profiles.active=prod"]