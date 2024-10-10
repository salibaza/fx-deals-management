FX Deals Management System

This system manages foreign exchange (FX) deal data, allowing users to submit and store FX deals in a MySQL database. It is built with Spring Boot, uses Hibernate for ORM, and stores deal data in a MySQL database. The application is containerized and can be run using Docker Compose.

System Design
The system consists of two main components:
1. Backend API: A Spring Boot application that exposes REST endpoints to accept and store FX deals in a MySQL database.
	- Deal Model: Represents the FX deal entity.
	- Deal Repository: Manages the persistence of deals in the MySQL database.
	- Deal Validation: Ensures the validity of deal data (e.g., currency codes, deal timestamps, etc.).
2. Database: MySQL is used to store the deal records. The init.sql script is automatically executed when the MySQL container is initialized to set up the database and tables.


Running the Project
Docker and Docker Compose must be installed on your machine.
Steps to Run the Project
1. Clone the repository by running the following command in git bash:
git clone https://github.com/salibaza/fx-deals-management.git
cd fx-deals-management
2. Build the project and install dependencies by running the following command in git bash:
mvn clean install
3. Run the application using Docker Compose by running the following command in git bash:
docker-compose up --build

This command will:
- Build the Spring Boot application and package it as a JAR file.
- Start the MySQL database and Spring Boot application as separate containers.
Note: The application will be accessible at http://localhost:8080 by default, and the MySQL server will be accessible at localhost:3307 (mapped from container port 3306).


API Usage
The system exposes an API to manage FX deals. Below are the details of the available API endpoints.

1. Add New Deal
Endpoint: POST /api/deals
Description: Submits a new FX deal to be persisted in the database.

Request Body:
json: {
  "dealUniqueId": "D12345",
  "fromCurrency": "USD",
  "toCurrency": "EUR",
  "dealTimestamp": "2024-10-10T12:00:00Z",
  "dealAmount": 1000.00
}

Sample cURL Request:
bash: curl -X POST http://localhost:8080/api/deals \
-H "Content-Type: application/json" \
-d '{
    "dealUniqueId": "D12345",
    "fromCurrency": "USD",
    "toCurrency": "EUR",
    "dealTimestamp": "2024-10-10T12:00:00Z",
    "dealAmount": 1000.00
}'

Response:
- 201 Created if the deal is successfully stored.
- 400 Bad Request if the request contains invalid data (e.g., missing fields, invalid currency codes).

2. Retrieve All Deals
Endpoint: GET /api/deals
Description: Retrieves a list of all FX deals stored in the database.

Sample cURL Request:
bash: curl http://localhost:8080/api/deals

Response:
json: [
  {
    "dealUniqueId": "D12345",
    "fromCurrency": "USD",
    "toCurrency": "EUR",
    "dealTimestamp": "2024-10-10T12:00:00Z",
    "dealAmount": 1000.00
  },
  {
    "dealUniqueId": "D12346",
    "fromCurrency": "GBP",
    "toCurrency": "USD",
    "dealTimestamp": "2024-10-10T15:00:00Z",
    "dealAmount": 500.00
  }
]


Viewing Logs
To view the logs of the running containers (both the Spring Boot application and the MySQL database), you can use the following command:
bash: docker-compose logs -f

This will continuously stream logs from both containers.

Logs for Specific Container
To view the logs for a specific container (e.g., MySQL or the Spring Boot application):
- For MySQL:
bash: docker logs deals-mysql-1

- For the Spring Boot App:
bash: docker logs deals-app-1


Running Tests
Unit Testing
Unit tests are included for validation logic, database insertions, and error handling.

1. Run Unit Tests Locally:
	If you are not using Docker, you can run unit tests using Maven:

	bash: mvn test

2. Run Unit Tests in Docker:
	If you are using Docker Compose, you can modify the Dockerfile to run tests during the build process (or create a separate container just for testing).


Manual Testing (with Mock Data)
You can manually test the application by submitting a sample file with mock deal data.

1. Update the mock-deals.json File: The mock-deals.json file located in src/test/resources/ contains sample data for testing. You can update this file to include the data you want to test manually. This file will be used by the DealControllerIntegrationTest class to simulate manual testing.
2. Run the Integration Test: The DealControllerIntegrationTest class is responsible for running integration tests using the mock data from the mock-deals.json file. You can run these tests using Maven:
bash: mvn test -Dtest=DealControllerIntegrationTest
3. Verify: After running the tests, you can check the database or logs to ensure that the mock data has been processed correctly.