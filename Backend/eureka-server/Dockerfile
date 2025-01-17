FROM eclipse-temurin:18-jdk-jammy as build

COPY src src

COPY gradlew gradlew.bat settings.gradle build.gradle ./

COPY gradle gradle

RUN ./gradlew build



FROM eclipse-temurin:18-jre-jammy

WORKDIR /home/app

ARG JAR_FILE=./build/libs/*.jar

COPY --from=build $JAR_FILE app.jar

EXPOSE 8761

HEALTHCHECK --interval=20s --timeout=10s --start-period=25s --retries=5 \
    CMD curl -f http://localhost:8761/actuator/health | grep UP || exit 1

ENTRYPOINT [ "java", "-jar", "app.jar"]