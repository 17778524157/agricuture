FROM openjdk:17-jdk-alpine

VOLUME /tmp

COPY target/agriculture-gateway-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
