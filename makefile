.PHONY: build docker up down test clean run

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

# Run app with a CSV file or single-line deal
# Usage:
#   make run FILE=/app/sample-data/deals-sample.csv
#   make run DEAL="sample-data/deals-sample2.csv"
# run: docker
# ifndef FILE
# ifndef DEAL
# 	$(error You must set FILE=<csv-path> or DEAL="<deal-string>")
# endif
# endif
# 	@echo "📦 Running app..."
# ifdef FILE
# 	docker-compose run --rm app /wait-for-it.sh db:5432 -- /app/bin/app $(FILE)
# endif
# ifdef DEAL
# 	docker-compose run --rm app /wait-for-it.sh db:5432 -- /app/bin/app "$(DEAL)"
# endif
# Run app with a CSV file or single-line deal fully inside Docker
# Usage:
#   make run FILE=/app/sample-data/deals-sample.csv
#   make run DEAL="sample-data/deals-sample2.csv"
run: docker
ifndef FILE
ifndef DEAL
	$(error You must set FILE=<csv-path> or DEAL="<deal-string>")
endif
endif
	@echo "📦 Running app inside Docker Compose..."
ifdef FILE
	docker-compose run --rm app /wait-for-it.sh db:5432 -- /app/bin/app $(FILE)
endif
ifdef DEAL
	docker-compose run --rm app /wait-for-it.sh db:5432 -- /app/bin/app "$(DEAL)"
endif