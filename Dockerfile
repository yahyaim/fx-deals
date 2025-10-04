FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install optional dependencies (netcat for wait-for-it)
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# Copy the pre-built app distribution
COPY app/build/install/app/ /app/

# Copy sample data and wait-for-it script
COPY sample-data /app/sample-data
COPY scripts/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Default argument for the app
ARG DEAL_INPUT=sample-data/deals-sample.csv
ENV DEAL_INPUT=$DEAL_INPUT

# Wait for DB, then run the app
CMD ["/wait-for-it.sh", "db:5432", "--", "/app/bin/app", "$DEAL_INPUT"]
    
