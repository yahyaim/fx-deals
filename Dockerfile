FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install netcat
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

COPY app/build/install/app/ /app/
COPY sample-data /app/sample-data
COPY scripts/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

CMD ["/wait-for-it.sh", "db:5432", "--", "/app/bin/app"]
