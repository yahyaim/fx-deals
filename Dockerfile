# # Use Java 21 runtime
# FROM eclipse-temurin:21-jre-jammy

# # Working directory inside container
# WORKDIR /app

# # Install dependencies
# RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# # Copy built app from Gradle
# COPY app/build/install/app/ /app/
# COPY sample-data /app/sample-data
# COPY scripts/wait-for-it.sh /wait-for-it.sh
# RUN chmod +x /wait-for-it.sh

# # Default argument (can be overridden at runtime)
# ARG DEAL_INPUT=sample-data/deals-sample.csv
# ENV DEAL_INPUT=$DEAL_INPUT

# # Wait for DB to be ready, then run app with argument
# CMD ["/wait-for-it.sh", "db:5432", "--", "bash", "-c", "/app/bin/app $DEAL_INPUT"]
# Build stage: Use full JDK to build the app
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copy Gradle wrapper and project files
COPY gradlew .
COPY gradle gradle
COPY app app
COPY build.gradle settings.gradle ./

# Build the app and install distribution
RUN ./gradlew :app:clean :app:installDist --no-daemon

# Runtime stage: Use minimal JRE
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install dependencies (optional, e.g., netcat)
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# Copy built app from the build stage
COPY --from=build /app/app/build/install/app/ /app/

# Copy sample data and scripts
COPY sample-data /app/sample-data
COPY scripts/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Default argument
ARG DEAL_INPUT=sample-data/deals-sample.csv
ENV DEAL_INPUT=$DEAL_INPUT

# Wait for DB then run app
CMD ["/wait-for-it.sh", "db:5432", "--", "/app/bin/app", "$DEAL_INPUT"]
