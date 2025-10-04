.PHONY: build docker up down clean test run

# -----------------------------
# Build the Java project
# -----------------------------
build:
	@echo "🔨 Building Java project..."
	./gradlew :app:clean :app:installDist --no-daemon

# -----------------------------
# Build Docker image
# -----------------------------
docker: build
	@echo "🐳 Building Docker image..."
	docker build -t fx-dwh .

# -----------------------------
# Start full Docker Compose stack
# -----------------------------
up: docker
	@echo "🚀 Starting Docker Compose stack..."
	docker compose up --build

# -----------------------------
# Stop Docker Compose stack
# -----------------------------
down:
	@echo "🛑 Stopping Docker Compose stack..."
	docker compose down -v

# -----------------------------
# Clean Gradle build artifacts
# -----------------------------
clean:
	@echo "🧹 Cleaning build artifacts..."
	./gradlew clean

# -----------------------------
# Run tests
# -----------------------------
test:
	@echo "✅ Running tests..."
	./gradlew test

# -----------------------------
# Run app with CSV file or single deal string
# Usage:
#   make run FILE=/path/to/file.csv
#   make run DEAL="D-1006,USD,EUR,2025-09-30T10:15:30Z,10000.50"
# -----------------------------
run: docker
ifndef FILE
ifndef DEAL
	$(error You must set FILE=<csv-path> or DEAL="<deal-string>")
endif
endif

ifdef FILE
	@echo "📦 Running app with CSV file: $(FILE)"
	docker compose run --rm -e DEAL_INPUT="$(FILE)" app
endif

ifdef DEAL
	@echo "📦 Running app with single deal: $(DEAL)"
	docker compose run --rm -e DEAL_INPUT="$(DEAL)" app
endif
