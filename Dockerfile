FROM eclipse-temurin:17-jdk-alpine

COPY ./build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Dspring.profiles.active=stage","-jar","/app.jar"]