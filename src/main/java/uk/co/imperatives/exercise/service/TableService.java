package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.repository.TableRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableService implements TableServiceInterface {

    private final TableRepository tableRepository;

    public Table addTable(int noOfSeats) {
        return tableRepository.save(Table.builder().noOfSeats(noOfSeats).noOfSeatsAllocated(0).build());
    }

    public Table addTable(int tableNumber, int noOfSeats) {
        // Check if a table with the same number already exists
        if (tableRepository.existsById(tableNumber)) {
            throw new TableAlreadyExistsException("Table with number " + tableNumber + " already exists.");
        }

        // Proceed to save the new table
        Table newTable = Table.builder()
                .number(tableNumber)
                .noOfSeats(noOfSeats)
                .noOfSeatsAllocated(0)
                .build();

        return tableRepository.save(newTable);
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
