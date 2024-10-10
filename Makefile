# Variables
DOCKER_COMPOSE = docker-compose
MVN = mvn
PROJECT_NAME = fx-deals-management

# Default command: runs the application
run: build
	$(DOCKER_COMPOSE) up --build

# Build the application with Maven and package it as a jar
build:
	$(MVN) clean install

# Run unit tests
test:
	$(MVN) test

# Run integration tests (e.g., DealControllerIntegrationTest)
integration-test:
	$(MVN) test -Dtest=DealControllerIntegrationTest

# Stop the running containers
stop:
	$(DOCKER_COMPOSE) down

# View logs for all containers
logs:
	$(DOCKER_COMPOSE) logs -f

# View logs for a specific service (app or mysql)
logs-app:
	$(DOCKER_COMPOSE) logs -f app

logs-mysql:
	$(DOCKER_COMPOSE) logs -f mysql

# Clean up Docker containers, volumes, and networks
clean:
	$(DOCKER_COMPOSE) down -v --remove-orphans
	docker system prune --volumes --force

# Manual test using mock data
manual-test:
	curl -X POST http://localhost:8080/api/deals \
	-H "Content-Type: application/json" \
	-d @src/test/resources/mock-deals.json

