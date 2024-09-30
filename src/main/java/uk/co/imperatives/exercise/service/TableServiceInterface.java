package uk.co.imperatives.exercise.service;

import uk.co.imperatives.exercise.model.Table;

import java.util.List;

public interface TableServiceInterface {
    Table addTable(int noOfSeats);

    Table addTable(int tableNumber, int noOfSeats);

    List<Table> getAllTables();

    void removeTable(int tableNumber);

    int getTableWithAvailableSeating(int noOfSeats);

    void decreaseOccupancy(int table, int noOfSeats);

    void increaseOccupancy(int table, int noOfSeats);

    boolean hasAvailability(int table, int noOfSeats);
}

