# Corporate Party Organizer Microservice

## Overview
This is a Spring Boot microservice designed to organize corporate parties by managing a guest list and tracking seating. The project implements a RESTful API for managing guests, their table assignments, and available seats.

## Requirements Met
- Guests can be added to the guest list with or without table assignments.
- The system ensures guests can only sit at tables with sufficient seating.
- Arrivals and departures of guests (and their accompanying friends) are handled.
- Tracks the number of empty seats in real-time.
- All requirements are covered by tests.

## Development Approach
This project was developed using **Test-Driven Development (TDD)**. Tests were written before implementing functionality, ensuring that all edge cases were considered and code was refactored for clarity after the tests passed. See the `test` directory for unit tests that validate the core business logic.

## How to Run the Project
### Prerequisites
- Java 21
- Maven
- Docker (if using containers)
- PostgreSQL (if using PostgreSQL for persistence)

### Running with Maven
To run the application locally:
```bash
mvn spring-boot:run