# Github-integration
Application used for fetching data from Github.

## General info
Application expose one endpoint using which we can collect available non fork repositories and corresponding branches by 
github user name. Response returned to client is in JSON format. Application has defined advice controller which handle
any exceptions during request processing. If requested user is not found appropriate message is return to the client.
Details exceptions messages are log using Slf4j.

Application uses Github API to collect data. Documentation for Github API can be find under that link:
https://docs.github.com/en/rest?apiVersion=2022-11-28


## Technologies
Main technologies:
* Java 21
* Spring Boot 3.2.2
* Lombok 1.18.30

## Setup
Setup details:
* set up Java runtime environment locally
* start application locally using: mvn spring-boot:run (by default application runs on port 8080)
* if you have problem with 'access denied' for file logback.xml you can change local permissions or remove this file for local deployment
* Example link to test endpoint locally: http://localhost:8080/api/repos/nonfork/user/eugenp