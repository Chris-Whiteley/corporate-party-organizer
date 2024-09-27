package uk.co.imperatives.exercise.service;

public interface TableServiceInterface {
    int getTableWithAvailableSeating(int noOfSeats);
    void decreaseOccupancy(int table, int noOfSeats);
    void increaseOccupancy(int table, int noOfSeats);
    boolean hasAvailability(int table, int noOfSeats);
}

