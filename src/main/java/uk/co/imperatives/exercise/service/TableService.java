package uk.co.imperatives.exercise.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.model.Table;

import java.util.List;

@Service
@Slf4j
public class TableService implements TableServiceInterface {

    public Table addTable(int noOfSeats) {
        return null;
    }

    public Table addTable(int tableNumber, int noOfSeats) {
        return null;
    }

    public List<Table> getAllTables() {
        return null;
    }

    @Override
    public void removeTable(int tableNumber) {

    }

    @Override
    public int getTableWithAvailableSeating(int noOfSeats) {
        return 0;
    }

    @Override
    public void decreaseOccupancy(int table, int noOfSeats) {
    }

    @Override
    public void increaseOccupancy(int table, int noOfSeats) {
    }

    @Override
    public boolean hasAvailability(int table, int noOfSeats) {
        return false;
    }
}
