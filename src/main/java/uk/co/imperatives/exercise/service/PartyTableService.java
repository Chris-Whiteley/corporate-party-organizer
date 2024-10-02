package uk.co.imperatives.exercise.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.exception.TableInUseException;
import uk.co.imperatives.exercise.model.PartyTable;
import uk.co.imperatives.exercise.repository.PartyTableRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyTableService implements PartyTableServiceInterface {

    private final PartyTableRepository partyTableRepository;

    @Override
    public PartyTable addTable(int noOfSeats) {
        if (noOfSeats <= 0) throw new IllegalArgumentException("Number of seats should be a number bigger than zero");
        
        // find next free table number
        int tableNo = 1;
        
        while (partyTableRepository.existsById(tableNo)) {
            tableNo++;
        }
        
        return partyTableRepository.save(PartyTable.builder().number(tableNo).noOfSeats(noOfSeats).noOfSeatsAllocated(0).build());
    }

    @Override
    public PartyTable addTable(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");
        if (noOfSeats <= 0) throw new IllegalArgumentException("Number of seats should be a number bigger than zero");

        // Check if a table with the same number already exists
        if (partyTableRepository.existsById(tableNumber)) {
            throw new TableAlreadyExistsException("Table with number " + tableNumber + " already exists.");
        }

        // Proceed to save the new table
        PartyTable newTable = PartyTable.builder()
                .number(tableNumber)
                .noOfSeats(noOfSeats)
                .noOfSeatsAllocated(0)
                .build();

        return partyTableRepository.save(newTable);
    }

    @Override
    public List<PartyTable> getAllTables() {
        return StreamSupport.stream(partyTableRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public void removeTable(int tableNumber) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        // Check if the table has allocated seats
        if (table.getNoOfSeatsAllocated() > 0) {
            throw new TableInUseException("Cannot delete table with allocated seats");
        }

        // Proceed with deletion if no allocated seats
        partyTableRepository.deleteById(tableNumber);
    }

    @Override
    public int getTableWithAvailableSeating(int noOfSeats) {
        var tableOptional =
                StreamSupport.stream(partyTableRepository.findAll().spliterator(), false)
                        .filter(table -> table.getUnAllocatedSeats() >= noOfSeats)
                        .findAny();

        return tableOptional.map(PartyTable::getNumber).orElse(0);
    }

    @Override
    public void decreaseOccupancy(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        table.setNoOfSeatsAllocated(table.getNoOfSeatsAllocated() - noOfSeats);
        partyTableRepository.save(table);

    }

    @Override
    public void increaseOccupancy(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        table.setNoOfSeatsAllocated(table.getNoOfSeatsAllocated() + noOfSeats);
        partyTableRepository.save(table);
    }

    @Override
    public boolean hasAvailability(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table with number " + tableNumber + " not found"));

        return table.getUnAllocatedSeats() >= noOfSeats;
    }
}
