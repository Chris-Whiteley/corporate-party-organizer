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
### User Story
**As an organiser,** I want to add guests to the guest list so that I can manage who is invited.

### Acceptance Criteria:
- The organiser can add a guest to the guest list by providing a name, table number, and the number of accompanying guests.
- The system will allow adding a guest more than once by updating the previous entry. This allows the organiser to modify the guest’s table or accompanying guests.
- If no table number is provided, the system should automatically assign the guest to the next available table.
- The organiser can add a guest even if they have no accompanying guests.
- The organiser can add multiple guests to the same table.

### Edge Cases:

**Duplicate Names:**
- The guest name must be unique.
- The system allows adding a guest more than once by updating the previous entry, enabling the organiser to modify the guest’s table or accompanying guests.
- The system allows correcting an existing guest name.

**Missing Table Number:**
- If no table number is provided, the system should assign the guest to the next available table with enough capacity.
- If no tables are available, the system should notify the organiser.

**Exceeding Table Capacity:**
- The system must ensure that adding a guest and their accompanying guests does not exceed the table's predefined capacity.
- If the capacity is exceeded, the system should either reject the addition or propose alternative tables.

**Invalid Input:**
- If an invalid table number is entered (e.g., negative or non-existent table), the system should display an error and prevent the addition.

**Empty or Missing Fields:**
- If the guest name is missing or blank, the system should reject the entry and display an error message.

**Zero or Negative Accompanying Guests:**
- The system should handle cases where accompanying guests are zero or negative by either rejecting or correcting the input.

**Case Sensitivity:**
- The system should decide whether guest names are case-sensitive or case-insensitive to avoid duplicate entries (e.g., "John Doe" vs. "john doe").

### Additional Considerations:
- Should the system handle extremely long guest names or names with special characters?

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