FROM openjdk:17

ARG JAR_FILE=WebFitnessTracker-1.0.0.jar

WORKDIR /app

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]