# https://github.com/GoogleCloudPlatform/openjdk-runtime
FROM openjdk:8u131-jdk-alpine

ADD ./server/build/libs/server.jar /bookstore/server.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/bookstore/server.jar"]
