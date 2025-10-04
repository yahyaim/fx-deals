.PHONY: docker up down run clean

# Build Docker image (app must already be built locally)
docker:
	@echo "🐳 Building Docker image..."
	docker build -t fx-dwh .

# Start full stack with Docker Compose (Postgres + app)
up: docker
	@echo "🚀 Starting Docker containers..."
	docker-compose up --build -d

# Stop and remove stack
down:
	@echo "🛑 Stopping Docker containers..."
	docker-compose down -v

# Run app fully containerized
# Usage:
#   make run FILE=sample-data/deals-sample.csv
#   make run DEAL="deal1,deal2"
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

