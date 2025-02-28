FROM eclipse-temurin:18-jre-jammy

WORKDIR /home/app

ARG JAR_FILE=./build/libs/*.jar

COPY $JAR_FILE app.jar

COPY temp temp

ENTRYPOINT ["java", "-jar", "app.jar"]