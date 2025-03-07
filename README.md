# Gaming Platform Social Service

A microservice component of the Gaming Platform that handles social events and interactions between players.

## Overview

The Social Service is responsible for managing and publishing social events such as:
- Friend requests
- Friend acceptances
- New follower notifications

## Features

- REST API endpoints for social event management
- Kafka integration for event publishing
- Event validation and error handling
- Comprehensive test coverage

## Prerequisites

- Java 21
- Maven 3.9+
- Kafka (running locally or accessible via network)
- Spring Boot 3.4.3

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/jaclondon11/gp-social-service.git
cd gp-social-service
```

2. Build the project:
```bash
./mvnw clean install
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

The service will start on port 8082.

## API Endpoints

### Social Events

#### Send Friend Request
```http
POST /api/v1/social/events/friend-request
Content-Type: application/json

{
    "requesterId": 123,
    "targetId": 456
}
```

#### Send Friend Acceptance
```http
POST /api/v1/social/events/friend-acceptance
Content-Type: application/json

{
    "acceptorId": 456,
    "requesterId": 123
}
```

#### Send New Follower
```http
POST /api/v1/social/events/new-follower
Content-Type: application/json

{
    "followerId": 789,
    "targetId": 123
}
```

## Configuration

The application can be configured through `application.yml`:

```yaml
server:
  port: 8082

spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

## Development

### Running Tests
```bash
./mvnw test
```

### Building
```bash
./mvnw clean package
```
