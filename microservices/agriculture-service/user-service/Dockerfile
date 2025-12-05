FROM openjdk:17-jdk-alpine

VOLUME /tmp

COPY target/user-service-1.0.0.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java","-jar","/app.jar"]
