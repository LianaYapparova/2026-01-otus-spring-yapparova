FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /bookstore

COPY pom.xml .
COPY src ./src

RUN mvn -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /bookstore

COPY --from=build /bookstore/target/*.jar bookstore.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "bookstore.jar"]