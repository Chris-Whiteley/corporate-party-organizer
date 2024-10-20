# Corporate Party Organiser Microservice

## Overview
This is a Spring Boot microservice designed to organise corporate parties by managing a guest list and tracking seating. The project implements a RESTful API for managing guests, their table assignments, and available seats.

## Requirements Met
- Guests can be added to the guest list with or without table assignments.
- The system ensures guests can only sit at tables with sufficient seating.
- Arrivals and departures of guests (and their accompanying guests) are handled, and guest arrival recording is idempotent, allowing the organiser to adjust the number of accompanying guests even if the guest has already arrived.
- Tracks the number of empty seats in real-time.
- All requirements are covered by tests.
- Invalid inputs (e.g., negative accompanying guests, exceeding table capacity) are handled consistently with meaningful error messages.

## Agile Approach

The development process followed an Agile-like methodology by breaking the project down into small, iterative tasks:
1. Set up initial Spring Boot skeleton with table and guest management.
2. Add table management logic, including seat validation and auto-assignment.
3. Implement core APIs (add guest, arrival, departure) with test-driven development.
4. Implement OpenAPI documentation for the REST API endpoints.
5. Add consistent error handling for invalid inputs and capacity constraints.

Each task was committed to version control with meaningful, descriptive commit messages.

### User Stories - Table Management
Note. Table Management was not specified in the Java Exercise's Sample API Guide but I have added it for completeness as table assignment and available seating for guests needs to be checked. 
### User Story
**As an organiser,** I want to add a table with a specified number of seats so that I can expand seating capacity for the event.

### Acceptance Criteria:
- The organiser can add a table by providing the number of seats.
- The system will automatically assign a unique table number if none is specified.
- The system ensures that a newly added table is available for assigning guests.

---

### User Story
**As an organiser,** I want to add a table with a specific table number and number of seats so that I can control table number assignment if I wish to.

### Acceptance Criteria:
- The organiser can add a table by providing both a table number and the number of seats.
- The system ensures that the provided table number is unique and does not conflict with existing tables.
- The system will add the table with the specified number of seats.

---

### User Story
**As an organiser,** I want to view a list of all tables so that I can manage seating arrangements effectively.

### Acceptance Criteria:
- The organiser can retrieve a list of all tables along with their respective seat capacities.
- The system displays information about each table, including the total number of seats and the number of seats currently allocated.

---

### User Story
**As an organiser,** I want to remove a specific table by table number so that I can update the seating arrangements when a table is no longer needed.

### Acceptance Criteria:
- The organiser can remove a table by specifying its table number.
- If the table has guests already assigned, the system will provide a warning and prevent the removal.
- If the table does not exist the user will be notified
- The system removes the table if no guests are assigned to it.

### Edge Cases:
**Invalid Input:**
- If no of seats is less than 1, the system should display an error and prevent the addition.
- If table number entered is less than 1, the system should display an error and prevent the addition.

**Removing an Allocated Table:**
- If the table is assigned to any guests, the system will prevent its removal and notify the organiser.
- If the specified table does not exist

### User Stories - Guest List Management
### User Story
**As an organiser,** I want to add guests to the guest list so that I can manage who is invited.

### Acceptance Criteria:
- The organiser can add a guest to the guest list by providing a name, table number, and the number of accompanying guests.
- The system will allow adding a guest more than once by updating the previous entry. This allows the organiser to modify the guest’s table or accompanying guests.
- If no table number is provided, the system should automatically assign a table, which has the required available seating, to the guest.
- The organiser can add a guest even if they have no accompanying guests.
- The organiser can add multiple guests to the same table.

### Edge Cases:

**Duplicate Names:**
- The guest name must be unique.
- Duplicate Names: Currently, guest names are used as unique identifiers. The system does not allow duplicate names, and any subsequent additions will update the existing guest's information. This may lead to issues with guests having the same name (consider extending the identifier in future versions).
- The system allows adding a guest more than once by updating the previous entry, enabling the organiser to modify the guest’s table or accompanying guests.
- The system allows correcting an existing guest's name.

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

**Negative Accompanying Guests:**
- The system should handle cases where accompanying guests is negative by either rejecting or correcting the input.

**Case Sensitivity:**
- The system should decide whether guest names are case-sensitive or case-insensitive to avoid duplicate entries (e.g., "John Doe" vs. "john doe").

### Additional Considerations:
- Should the system handle extremely long guest names or names with special characters?

### User Story
**As a guest,** I want to be able to arrive at a party with extra friends, so I can join if there is space.

### User Story
**As an organiser,** I want to allow changes to the number of accompanying guests even after a guest has arrived to accommodate late changes.


### Acceptance Criteria:
- The organiser can update the number of accompanying guests for a guest even after their arrival has been recorded.
- The system does not throw an error if the guest has already arrived; instead, it updates the accompanying guests.


## Running the Application

### Prerequisites
- Java 21
- Maven
- Docker

Ensure **Docker Engine** is running to support the PostgreSQL database in the development profile.

### Running with PostgreSQL (Development Profile)

#### To run both the application and PostgreSQL in Docker containers, follow these steps:

1. **Start the PostgreSQL and App Containers**  
   Navigate to the project root directory and run:
   ```bash
   docker-compose up --build -d
   ```
   This will rebuild the application and start both the PostgreSQL and spring-app containers.

2. **To view the application log**  
   Use:
   ```bash
   docker logs spring-app
   ```

3. **Access the PostgreSQL Database via psql**  
   You can interact with the running PostgreSQL database using `psql`:
   ```bash
   docker exec -it dev-postgres psql -U dev_user -d dev_party_db
   ```

4. **Shut Down the PostgreSQL and App Containers**  
   To stop the containers, use:
   ```bash
   docker-compose down
   ```

#### To run the application locally and PostgreSQL in a Docker container, follow these steps:

1. **Start the PostgreSQL Container**  
   Navigate to the project root directory and run:
   ```bash
   docker-compose up -d postgres
   ```
   This command will start the PostgreSQL container in detached mode.

2. **Run the Microservice locally**  
   To start the microservice locally using the `dev` profile (with PostgreSQL), run:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### Running with H2 (Default Profile)

If you prefer to use the default in-memory H2 database, simply run the following command:
```bash
./mvnw spring-boot:run
```

## Accessing the Microservice

- The microservice is configured to run on **port 8090**.

- To access the API documentation (Swagger UI), visit:
  ```
  http://localhost:8090/api-docs.html
  ```
  This documentation is available regardless of whether you're running with the default H2 database or the `dev` profile with PostgreSQL.

  You can use this Swagger UI to explore and test the APIs. Alternatively, you can use tools like **curl** or **Postman**.

## Development Information

- **Default Profile**: Runs with an in-memory H2 database.
- **Dev Profile**: Runs with a PostgreSQL database in a Docker container.

This setup provides flexibility for different environments, and the application can be run locally with H2 or in development mode with PostgreSQL.

## Assumptions

- **Table Management**: The system includes table management, allowing for the dynamic creation and adjustment of tables and their seating capacities.
- **Table number**: Tables are identified with a positive integer number

## Future Improvements

- **Guest Name**: Is this enough to identify a guest?  Could get duplication using just their name.
- **Guest and friends can only have one table**: This could be a limitation for a large group if they could fit in over more than one table.
- **Can only do one party**: Currently Can only set up one party.  What if you want to organise more than one. Perhaps have ability to set up more than one party and specify the location (venue) and date.  Then tables would be for a particular venue.  A party would have a date and a venue.  Also perhaps have the ability to create invites.  This would make the application more complicated and the table primary keys would change.  Alternatively, add the extra party information as configuration and just run up a new microservice.
- **Table Plan**: Could provide visual layout of the tables for guests to decide which table they would like.