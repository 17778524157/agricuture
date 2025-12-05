FROM openjdk:17-jdk-alpine

VOLUME /tmp

COPY target/farm-service-1.0.0.jar app.jar

EXPOSE 8002

ENTRYPOINT ["java","-jar","/app.jar"]
