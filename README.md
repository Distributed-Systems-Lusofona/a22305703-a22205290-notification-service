# Notification Service

This repository contains the Notification Service, one of the microservices in the Store Project —
a distributed system composed of multiple Spring Boot services that work together to manage an online store.

Each microservice runs in its own container, communicating with others via REST APIs.

## Setup Store network

````
docker network create store-network
````