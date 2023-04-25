FROM eclipse-temurin:17-jdk-alpine
MAINTAINER "Wee Hong KOH"

COPY ./build/libs/*.jar app.jar

ENTRYPOINT ["java","-Dspring.profiles.active=stage","-jar","/app.jar"]