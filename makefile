.PHONY: build docker up down test clean

# Build the Gradle project and installDist
build:
	@echo "🔨 Building Java project..."
	./gradlew :app:clean :app:installDist

# Build Docker image for the app
docker: build
	@echo "🐳 Building Docker image..."
	docker build -t fx-dwh .

# Run the full docker-compose stack (Postgres + app)
up: docker
	@echo "🚀 Starting Docker containers..."
	docker-compose up --build

# Stop and remove docker-compose stack
down:
	@echo "🛑 Stopping Docker containers..."
	docker-compose down -v

# Run unit tests
test:
	@echo "✅ Running tests..."
	./gradlew test

# Clean build artifacts
clean:
	@echo "🧹 Cleaning project..."
	./gradlew clean
