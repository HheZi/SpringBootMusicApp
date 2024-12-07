FROM openjdk:latest

ARG JAR_FILE=./build/libs/*.jar

WORKDIR /home/app
COPY $JAR_FILE app.jar
COPY ./audio ./audio

ENTRYPOINT [ "java", "-jar", "app.jar"]