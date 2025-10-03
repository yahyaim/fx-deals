.PHONY: build run up down test

build:
	./gradlew clean shadowJar

up: build
	docker-compose up --build

down:
	docker-compose down -v

run-local: build
	java -jar build/libs/fx-deals-ingest-0.1.0-all.jar sample-data/deals-sample.csv

test:
	./gradlew test
