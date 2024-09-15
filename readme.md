# Bank Transfer Service

This is a Spring Boot application to simulate money transfers between bank accounts.

## Features
1. Money Transfer REST endpoint.
2. Retrieve transactions with optional filters (status, account number, date range).
3. Scheduled task to update commission for successful transactions.
4. Scheduled task to produce daily transaction summary.

## Tech Stack
- Java 11+
- Spring Boot
- JPA/Hibernate
- H2 (In-memory database for testing)

## How to Run
1. Clone the repository.
2. Run `mvn clean install.`
3. Run the application using `mvn spring-boot:run`.
4. opy and paste this link to the browser to see the API Documentation Open API Swagger
   http://localhost:8989/swagger-ui/index.html#/
5. Use Postman or Curl to interact with the endpoints.

## Create account 
- POST `/api/accounts`: Create a new account for transaction.
```
{
    "accountNumber": "8978822661",
    "balance": 6030
}
```
- GET `/api/accounts`: Get all the account and balance

## Transaction Endpoints
- POST `/api/transactions/transfer`: Create a new transfer.
```aiignore
{
    "accountNumberFrom": "8978822660",
    "accountNumberTo": "8978822661",
    "amount": 400
}
```
- GET `/api/transactions`: Get a list of transactions with optional filters.
- GET `/api/transactions/summary`: Get a summary of transactions for a specific day.


## Build and Run with Docker

Build the Docker image:

``` docker build -t money-transfer-service . ```

Run the Docker container:

``` docker run -p 8989:8989 money-transfer-service ```

# kubernetes deployment

## Start Minikube:

``minikube start ``

## Point Docker CLI to Minikube’s Docker daemon:

`` eval $(minikube docker-env) ``

## Build the Docker image inside Minikube:

``` docker build -t money-transfer-service .```


## Apply Kubernetes manifests:
```aiignore
kubectl apply -f deployment.yaml
kubectl apply -f configmap.yaml
```

## Get the Minikube IP and access the application:
```aiignore
minikube service money-transfer-service

```
## Running Tests
To run the unit tests, use the following command:
```bash
mvn test
