FROM eclipse-temurin:18-jre-jammy

WORKDIR /home/app

ARG JAR_FILE=./build/libs/*.jar

COPY $JAR_FILE app.jar

COPY images images

COPY audio audio

ENTRYPOINT ["java", "-jar", "app.jar"]