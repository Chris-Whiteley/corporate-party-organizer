package uk.co.imperatives.exercise.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.exception.TableInUseException;
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
    public void removeTable(int tableNumber) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        Table table = tableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        // Check if the table has allocated seats
        if (table.getNoOfSeatsAllocated() > 0) {
            throw new TableInUseException("Cannot delete table with allocated seats");
        }

        // Proceed with deletion if no allocated seats
        tableRepository.deleteById(tableNumber);
    }

    @Override
    public int getTableWithAvailableSeating(int noOfSeats) {
        var tableOptional =
                StreamSupport.stream(tableRepository.findAll().spliterator(), false)
                        .filter(table -> table.getUnAllocatedSeats() >= noOfSeats)
                        .findAny();

        return tableOptional.map(Table::getNumber).orElse(0);
    }

    @Override
    public void decreaseOccupancy(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        Table table = tableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        table.setNoOfSeatsAllocated(table.getNoOfSeatsAllocated() - noOfSeats);
        tableRepository.save(table);

    }

    @Override
    public void increaseOccupancy(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        Table table = tableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        table.setNoOfSeatsAllocated(table.getNoOfSeatsAllocated() + noOfSeats);
        tableRepository.save(table);
    }

    @Override
    public boolean hasAvailability(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        Table table = tableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        return table.getUnAllocatedSeats() >= noOfSeats;
    }
}
