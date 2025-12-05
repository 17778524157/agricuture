FROM openjdk:17-jdk-alpine

VOLUME /tmp

COPY target/notification-service-1.0.0.jar app.jar

EXPOSE 8005

ENTRYPOINT ["java","-jar","/app.jar"]
