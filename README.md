FX Deals Management System

This system manages foreign exchange (FX) deal data, allowing users to submit and store FX deals in a MySQL database. It is built with Spring Boot, uses Hibernate for ORM, and stores deal data in a MySQL database. The application is containerized and can be run using Docker Compose.

System Design
The system consists of two main components:
1. Backend API: A Spring Boot application that exposes REST endpoints to accept and store FX deals in a MySQL database.
   - Deal Model: Represents the FX deal entity.
   - Deal Repository: Manages the persistence of deals in the MySQL database.
   - Deal Processing: Handles the business logic and ensures valid deals are persisted.
   - Deal Validation: Ensures the validity of deal data, including currency codes, deal timestamps, etc. A separate CurrencyValidationService validates ISO 4217 currency codes.
   - Global Exception Handling: Centralized error handling to manage validation and other exceptions.
2. Database: MySQL is used to store the deal records. The init.sql script is automatically executed when the MySQL container is initialized to set up the database and tables.


Running the Project
Prerequisites:
- Docker and Docker Compose must be installed on your machine.
- Docker Desktop must be running on your system (Windows/macOS) before proceeding.
- Make must be installed to use the make commands in the Makefile.

Steps to Run the Project
1. Clone the repository (by running the following command in git bash):
git clone https://github.com/salibaza/fx-deals-management.git
cd fx-deals-management
2. Build the project and install dependencies (by running the following command in git bash):
mvn clean install
3. Run the application using Docker Compose (by running the following command in git bash):
docker-compose up --build
This command will:
   - Build the Spring Boot application and package it as a JAR file.
   - Start the MySQL database and Spring Boot application as separate containers. 
Note: Ensure Docker Desktop is running on your machine before running the above command.
The application will be accessible at localhost:8080 by default, and the MySQL server will be accessible at localhost:3307 (mapped from container port 3306).
4. Ensure the Spring Boot Application is Running: After running the project, you should be able to access the API at http://localhost:8080/api/deals. Ensure there are no errors in the console logs, and that the Spring Boot application is running correctly.


Running Makefile Commands
You can also manage and automate tasks using the Makefile. Ensure that make is installed, then use the following commands:
1. Run the application:
bash: make run
This builds the application and starts Docker containers with docker-compose up --build.
2. Build the application:
bash: make build
This command runs mvn clean install to build the application.
3. Run unit tests:
bash: make test
4. Run integration tests:
bash: make integration-test
5. Stop running containers:
bash: make stop
6. Clean up Docker containers, volumes, and networks:
bash: make clean
This stops all running containers, removes them, and performs Docker system pruning.
7. Manually test with mock data: Update the src/test/resources/mock-deals.json file with mock FX deal data and then run:
bash: make manual-test


API Usage
The system exposes an API to manage FX deals.

1. Add New Deals
Endpoint: POST /api/deals
Description: Submits one or more FX deals to be persisted in the database.

Request Body:
json: [
{
"uniqueId": "D12345",
"fromCurrency": "USD",
"toCurrency": "EUR",
"dealTimestamp": "2024-10-10T12:00:00Z",
"dealAmount": 1000.00
}
]

Sample cURL Request:
bash: curl -X POST http://localhost:8080/api/deals \
-H "Content-Type: application/json" \
-d '[{
    "uniqueId": "D12345",
    "fromCurrency": "USD",
    "toCurrency": "EUR",
    "dealTimestamp": "2024-10-10T12:00:00Z",
    "dealAmount": 1000.00
}]'

Response:
- 201 Created if the deals are successfully processed and stored.
- 400 Bad Request if the request contains invalid data (e.g., missing fields, invalid currency codes). In this case, only valid deals will be processed and stored, while invalid ones will return error messages.
- 500 Internal Server Error: If an unexpected error occurs during processing.

Example Response (when some deals are valid and others are invalid):
Deal with UniqueId D12345 processed successfully.
Error processing Deal with UniqueId D12346: Invalid To Currency. Not a valid ISO 4217 currency code.

2. Retrieve All Deals
Endpoint: GET /api/deals
Description: Retrieves a list of all FX deals stored in the database.

Sample cURL Request:
bash: curl http://localhost:8080/api/deals

Response:
- 200 OK: If the list of FX deals is successfully retrieved.
    json: [
      {
        "uniqueId": "D12345",
        "fromCurrency": "USD",
        "toCurrency": "EUR",
        "dealTimestamp": "2024-10-10T12:00:00Z",
        "dealAmount": 1000.00
      }
    ]
- 204 No Content: If there are no deals available to retrieve.
- 500 Internal Server Error: If an unexpected error occurs during data retrieval.


Exception Handling
The application includes a Global Exception Handler that provides consistent responses for validation and unexpected errors:
- DealValidationException: Thrown when deal validation fails.
- RetrievalException: Handles errors that occur during deal retrieval.
- General Exception: Captures any unexpected errors during deal processing.

For example, if a validation error occurs, the response will be:
Error processing Deal with UniqueId D12345: Invalid To Currency. Not a valid ISO 4217 currency code.


Viewing Logs
To view the logs of the running containers:
- For all containers:
bash: docker-compose logs -f
or bash: make logs

- Logs for Specific Container
  - For MySQL:
  bash: docker logs deals-mysql-1
  bash: make logs-mysql

  - For the Spring Boot App:
  bash: docker logs deals-app-1
  bash: make logs-app


Running Tests
Unit Testing
Unit tests are included for validation logic, database insertions, retrieval functionality, and error handling.
1. Run Unit Tests Locally:
    bash: mvn test
    bash: make test
2. Run Unit Tests in Docker:
	If you are using Docker Compose, you can modify the Dockerfile to run tests during the build process or create a separate container just for testing.
Resetting the Database Before Tests
- The database is reset before each test run to ensure no data persists across tests.
- This ensures that the application behaves as expected and tests do not interfere with one another.

Manual Testing (with Mock Data)
You can manually test the application by submitting a sample file with mock deal data.
1. Update the mock-deals.json File:
   - The mock-deals.json file located in src/test/resources/ contains sample data for testing. You can update this file to include the data you want to test manually.
2. Use the following command to manually test the API with mock data:
bash: curl -X POST http://localhost:8080/api/deals \
   -H "Content-Type: application/json" \
   -d @src/test/resources/mock-deals.json
bash: make manual-test

Cleaning and Resetting the Environment
To stop the running containers and clean up Docker containers, volumes, and networks:
bash: docker-compose down -v --remove-orphans
docker system prune --volumes --force
bash: make clean


Stopping Running Containers
To stop the running Docker containers, you can use either of the following methods:
Using Docker Compose: Run the following command to stop and remove all containers, networks, and volumes created by Docker Compose:
bash: docker-compose down
Using Make: If you have make installed, you can use the predefined stop target in the Makefile:
bash: make stop
Both of these commands will gracefully stop the running containers, ensuring that no data is lost from volumes (unless explicitly removed).