FROM eclipse-temurin:17

WORKDIR /app

COPY build/libs/*.jar app.jar

COPY entrypoint.sh entrypoint.sh

RUN chmod +x entrypoint.sh

COPY audiveris audiveris

RUN chmod +x /app/audiveris/bin/Audiveris

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]