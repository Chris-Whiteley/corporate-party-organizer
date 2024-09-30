package uk.co.imperatives.exercise.service;

import uk.co.imperatives.exercise.model.PartyTable;

import java.util.List;

public interface PartyTableServiceInterface {
    PartyTable addTable(int noOfSeats);

    PartyTable addTable(int tableNumber, int noOfSeats);

    List<PartyTable> getAllTables();

    void removeTable(int tableNumber);

    int getTableWithAvailableSeating(int noOfSeats);

    void decreaseOccupancy(int table, int noOfSeats);

    void increaseOccupancy(int table, int noOfSeats);

    boolean hasAvailability(int table, int noOfSeats);
}

