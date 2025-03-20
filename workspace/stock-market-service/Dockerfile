FROM openjdk:17-jdk-slim

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY target/stock-market-service-0.0.1-SNAPSHOT.jar stock-market-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "stock-market-service.jar","--spring.profiles.active=docker"]
