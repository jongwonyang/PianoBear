FROM amazoncorretto:17

ARG JAR_FILE_PATH=backend/application/build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]

