package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.repository.TableRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableService implements TableServiceInterface {

    private final TableRepository tableRepository;

    @Override
    public Table addTable(int noOfSeats) {
        if (noOfSeats <= 0) throw new IllegalArgumentException("Number of seats should be a number bigger than zero");
        return tableRepository.save(Table.builder().noOfSeats(noOfSeats).noOfSeatsAllocated(0).build());
    }

    @Override
    public Table addTable(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");
        if (noOfSeats <= 0) throw new IllegalArgumentException("Number of seats should be a number bigger than zero");

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

    @Override
    public List<Table> getAllTables() {
        return StreamSupport.stream(tableRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Table removeTable(int tableNumber) {
return null;
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
