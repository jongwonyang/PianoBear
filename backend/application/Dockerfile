FROM eclipse-temurin:17

WORKDIR /app

COPY build/libs/*.jar app.jar

RUN mkdir -p /app/uploads && chown -R root:root /app/uploads && chmod -R 755 /app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]