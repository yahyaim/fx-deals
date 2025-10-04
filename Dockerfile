# =====================================================
# Stage 1: Build the Java app with Gradle
# =====================================================
FROM gradle:8.8-jdk21-jammy AS builder

WORKDIR /app
COPY . .

# Build and install the application distribution
RUN gradle :app:clean :app:installDist --no-daemon

# =====================================================
# Stage 2: Run the built app with JRE only
# =====================================================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install small dependencies (wait-for-it)
RUN apt-get update && apt-get install -y --no-install-recommends netcat bash && rm -rf /var/lib/apt/lists/*

# Copy built app and assets
COPY --from=builder /app/app/build/install/app /app/
COPY sample-data /app/sample-data
COPY scripts/wait-for-it.sh /wait-for-it.sh

RUN chmod +x /wait-for-it.sh /app/bin/app

# Default input argument (can be overridden by env)
ENV DEAL_INPUT="/app/sample-data/deals-sample.csv"

# ENTRYPOINT: wait for DB if DB_HOST is defined, otherwise skip
ENTRYPOINT ["bash", "-c", "\
if [ -n \"$DB_HOST\" ]; then \
  /wait-for-it.sh $DB_HOST:$DB_PORT -- /app/bin/app \"$DEAL_INPUT\"; \
else \
  /app/bin/app \"$DEAL_INPUT\"; \
fi \
"]

# Default CMD (for Compose override)
CMD []