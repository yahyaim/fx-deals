FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY build/libs/fx-deals-ingest-0.1.0-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
# Note: It’s a CLI app that takes a CSV path arg. Expose 8080 only if you add HTTP endpoints later.
