# Corporate Party Organizer Microservice

## Overview
This is a Spring Boot microservice designed to organize corporate parties by managing a guest list and tracking seating. The project implements a RESTful API for managing guests, their table assignments, and available seats.

## Requirements Met
- Guests can be added to the guest list with or without table assignments.
- The system ensures guests can only sit at tables with sufficient seating.
- Arrivals and departures of guests (and their accompanying friends) are handled.
- Tracks the number of empty seats in real-time.
- All requirements are covered by tests.

## Agile Approach

The development process followed an Agile-like methodology by breaking the project down into small, iterative tasks:
1. Set up initial Spring Boot skeleton with table and guest management.
2. Add table management logic, including seat validation and auto-assignment.
3. Implement core APIs (add guest, arrival, departure) with test-driven development.
4. Implement OpenAPI documentation for the REST API endpoints.
5. Add consistent error handling for invalid inputs and capacity constraints.

Each task was committed to version control with meaningful, descriptive commit messages.

### User Stories
As an organiser, I want to add guests to the guest list so that I can manage who is invited.
As a guest, I want to be able to arrive at a party with extra friends, so I can join if there is space.
As an organiser, I want to see how many seats are available, so I can plan the event.

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
```

## Assumptions

- **Table Management**: The system includes table management, allowing for the dynamic creation and adjustment of tables and their seating capacities.
- **Table id**: Tables are identified with a positive integer number

## Future Improvements

- **Guest Name**: Is this enough to identify a guest?  Could get duplication using just their name.
- **Can only do one party**: Currently Can only set up one party.  What if you want to organise more than one. Perhaps have ability to set up more than one party and specify the location (venue).  Then tables would be for a particular venue.  A party would have a date and a venue.  Also perhaps have the ability to create invites.  This would make the application more complicated and the table primary keys would change.