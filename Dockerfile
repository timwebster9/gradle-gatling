FROM openjdk:8-jre-alpine

COPY build/libs/gradle-gatling-0.0.1.jar /app/

CMD ["java", "-jar", "/app/gradle-gatling-0.0.1.jar"]