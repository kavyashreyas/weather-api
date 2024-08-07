# Spring Boot 3 weather-api Project with Spring Security and API-Docs

This project is a SpringBoot application and an HTTP REST API that fronts the
OpenWeatherMap service.

# Problem Statement
Develop SpringBoot application and test a HTTP REST API in that fronts the
OpenWeatherMap service: OpenWeatherMap name service
guide: http://openweathermap.org/current#name .
(Example: http://samples.openweathermap.org/data/2.5/weather?q=London,uk)
Your service should:
1. Enforce API Key scheme. An API Key is rate limited to 5 weather reports an hour.
   After that your service should respond in a way which communicates that the
   hourly limit has been exceeded. Create 5 API Keys. Pick a convention for handling
   them that you like; using simple string constants is fine. This is NOT an exercise
   about generating and distributing API Keys. Assume that the user of your service
   knows about them.
2. Have a URL that accepts both a city name and country name. Based upon these
   inputs, and the API Key, your service should decide whether or not to call the
   OpenWeatherMap name service. If it does, the only weather data you need to
   return to the client is the description field from the weather JSON result.
   Whether it does or does not, it should respond appropriately to the client.
3. Reject requests with invalid input or missing API Keys.
4. Store the data from openweathermap.org into H2 DB.
5. The API will query the data from H2
6. Clear Spring Layers are needed.
7. Follow Rest API convention.

## Prerequisites

- Java 17 or higher
- Maven 3 or higher

## Getting Started

1. Clone this repository.
   ```
   git clone https://github.com/
   ```

2. Navigate to the project folder.
    ```
   cd weather-api
   ```

3. Build the project.
    ```
   mvn clean install
   ```

4. Run the project.
    ```
   mvn spring-boot:run
   ```

5. Access the API-Docs at [http://localhost:8080/api-docs].

## Features

- Spring Security for authentication and authorization.
- Swagger UI for API documentation using OpenAPI 3.

## Usage

1. Run the application.
2. Access the Swagger API-Docs at [http://localhost:8080/api-docs].
3. Sample cURL to test:
   curl --location 'http://localhost:8080/api/weather?city=Sydney&country=Australia' \
   --header 'API-Key: 1913121308d289a14269b65ba700bd31' \
   --header 'Authorization: Basic dXNlcjpwYXNzd29yZA=='