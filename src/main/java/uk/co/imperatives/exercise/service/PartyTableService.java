package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.imperatives.exercise.dto.GuestsAtTable;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.exception.TableInUseException;
import uk.co.imperatives.exercise.exception.TableNotFoundException;
import uk.co.imperatives.exercise.model.GuestListEntry;
import uk.co.imperatives.exercise.model.PartyTable;
import uk.co.imperatives.exercise.repository.GuestListEntryRepository;
import uk.co.imperatives.exercise.repository.PartyTableRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class PartyTableService implements PartyTableServiceInterface {

    private final PartyTableRepository partyTableRepository;
    private final GuestListEntryRepository guestListEntryRepository;

    @Override
    @Transactional
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
    @Transactional
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
    @Transactional(readOnly = true)
    public List<PartyTable> getAllTables() {
        return StreamSupport.stream(partyTableRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeTable(int tableNumber) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new TableNotFoundException("Table with number " + tableNumber + " not found"));

        // Check if the table has allocated seats
        if (table.getNoOfSeatsAllocated() > 0) {
            throw new TableInUseException("Cannot delete table with allocated seats");
        }

        // Proceed with deletion if no allocated seats
        partyTableRepository.deleteById(tableNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTableWithAvailableSeating(int noOfSeats) {
        var tableOptional =
                StreamSupport.stream(partyTableRepository.findAll().spliterator(), false)
                        .filter(table -> table.getUnAllocatedSeats() >= noOfSeats)
                        .findAny();

        return tableOptional.map(PartyTable::getNumber).orElse(0);
    }

    @Override
    @Transactional
    public void decreaseOccupancy(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new TableNotFoundException("Table with number " + tableNumber + " not found"));

        table.setNoOfSeatsAllocated(table.getNoOfSeatsAllocated() - noOfSeats);
        partyTableRepository.save(table);

    }

    @Override
    @Transactional
    public void increaseOccupancy(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new TableNotFoundException("Table with number " + tableNumber + " not found"));

        table.setNoOfSeatsAllocated(table.getNoOfSeatsAllocated() + noOfSeats);
        partyTableRepository.save(table);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAvailability(int tableNumber, int noOfSeats) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");

        PartyTable table = partyTableRepository.findById(tableNumber)
                .orElseThrow(() -> new TableNotFoundException("Table with number " + tableNumber + " not found"));

        return table.getUnAllocatedSeats() >= noOfSeats;
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalEmptySeats() {
        var tableList =
                StreamSupport.stream(partyTableRepository.findAll().spliterator(), false)
                        .toList();

        log.debug("{}", tableList);

        return StreamSupport.stream(partyTableRepository.findAll().spliterator(), false)
                .mapToInt(PartyTable::getUnAllocatedSeats)
                .sum();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuestsAtTable> getGuestsAtAllTables() {
        Map<Integer, Collection<String>> tableGuestsMap = new HashMap<>();

        guestListEntryRepository.findAll()
                .forEach(guestListEntry -> {
                    if (!guestListEntry.hasLeft()) {
                        var guestsAtTable = tableGuestsMap.computeIfAbsent(guestListEntry.getTableNumber(), k -> new ArrayList<>());
                        guestsAtTable.add(guestListEntry.getName());
                    }
                });

        // Add blank entries for empty tables
        StreamSupport.stream(partyTableRepository.findAll().spliterator(), false)
                .filter(partyTable -> partyTable.getNoOfSeatsAllocated() == 0)
                .forEach(partyTable -> {
                    if (!tableGuestsMap.containsKey(partyTable.getNumber())) {
                        tableGuestsMap.put(partyTable.getNumber(), Collections.emptyList());
                    }
                });

        return tableGuestsMap
                .entrySet()
                .stream()
                .map(entry -> GuestsAtTable.builder().tableNumber(entry.getKey()).guests(entry.getValue()).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public GuestsAtTable getGuestsAtTable(int tableNumber) {
        if (!tableExists(tableNumber)) {
            throw new TableNotFoundException("Table with number " + tableNumber + " not found");
        }

        List<String> guests =
                StreamSupport.stream(guestListEntryRepository.findAll().spliterator(), false)
                        .filter(guestListEntry -> guestListEntry.getTableNumber() == tableNumber)
                        .filter(guestListEntry -> !guestListEntry.hasLeft())
                        .map(GuestListEntry::getName)
                        .toList();

        return GuestsAtTable.builder().tableNumber(tableNumber).guests(guests).build();
    }

    private boolean tableExists(int tableNumber) {
        if (tableNumber <= 0) throw new IllegalArgumentException("Table number should be a number bigger than zero");
        return partyTableRepository.existsById(tableNumber);
    }

}
