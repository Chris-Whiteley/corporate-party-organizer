package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.imperatives.exercise.dto.GuestsAtTable;
import uk.co.imperatives.exercise.exception.*;
import uk.co.imperatives.exercise.model.GuestListEntry;
import uk.co.imperatives.exercise.repository.GuestListEntryRepository;
import uk.co.imperatives.exercise.validation.NameValidator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GuestListService implements GuestListServiceInterface {
    private final GuestListEntryRepository guestListEntryRepository;
    private final PartyTableServiceInterface tableService;

    @Override
    @Transactional
    public GuestListEntry addGuest(AddGuestRequest request) {
        // Check if the guest already exists
        var existingGuestOptional = guestListEntryRepository.findById(request.getName());

        // Build the guest to add
        var guestToAddBuilder = GuestListEntry.builder()
                .name(request.getName())
                .accompanyingGuests(request.getAccompanyingGuests());

        // Handle existing guest case
        if (existingGuestOptional.isPresent()) {
            var existingGuest = existingGuestOptional.get();

            if (existingGuest.hasLeft()) {
                throw new GuestHasLeftException("Cannot update the information of a guest that has left the party");
            }

            // Temporarily remove existing guests from the table
            tableService.decreaseOccupancy(existingGuest.getTableNumber(), existingGuest.noOfGuests());

            // Get a suitable table with availability
            var tableWithAvailability = getTableWithAvailability(request.getTable(), request.noOfGuests());
            if (tableWithAvailability == 0) {
                // No table with availability found, restore existing guests to table and throw exception
                tableService.increaseOccupancy(existingGuest.getTableNumber(), existingGuest.noOfGuests());
                throwNoAvailabilityException(request);
            }

            tableService.increaseOccupancy(request.getTable(), request.noOfGuests());
            guestToAddBuilder.tableNumber(tableWithAvailability);
        } else {
            // Handle new guest case
            var tableWithAvailability = getTableWithAvailability(request.getTable(), request.noOfGuests());
            if (tableWithAvailability == 0) {
                throwNoAvailabilityException(request);
            }
            tableService.increaseOccupancy(tableWithAvailability, request.noOfGuests());
            guestToAddBuilder.tableNumber(tableWithAvailability);
        }

        return guestListEntryRepository.save(guestToAddBuilder.build());
    }

    @Override
    @Transactional
    public GuestListEntry updateName(String oldName, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new NameValidationError("Name cannot be null or empty");
        }

        if (!NameValidator.isValidName(newName)) {
            throw new NameValidationError(NameValidator.validate(newName).message);
        }

        // Find the guest by the old name
        Optional<GuestListEntry> existingGuestOpt = guestListEntryRepository.findById(oldName);

        if (existingGuestOpt.isEmpty()) {
            throw new GuestNotFoundException("Guest with name " + oldName + " not found");
        }

        // check if the newName is already being used by an existing guest
        if (guestListEntryRepository.existsById(newName)) {
            throw new GuestAlreadyExistsException("Guest with name " + newName + " already exists");
        }

        // Retrieve the existing guest
        GuestListEntry existingGuestListEntry = existingGuestOpt.get();

        // Delete the guest with the old name
        guestListEntryRepository.delete(existingGuestListEntry);

        // Create a new guest with the new name but keep other details the same
        GuestListEntry updatedGuestListEntry = GuestListEntry.builder()
                .name(newName)
                .tableNumber(existingGuestListEntry.getTableNumber())
                .accompanyingGuests(existingGuestListEntry.getAccompanyingGuests())
                .timeArrived(existingGuestListEntry.getTimeArrived())
                .timeLeft(existingGuestListEntry.getTimeLeft())
                .build();

        // Save the new guest
        guestListEntryRepository.save(updatedGuestListEntry);

        // Return the newly created guest
        return updatedGuestListEntry;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuestListEntry> getAllGuests() {
        return StreamSupport.stream(guestListEntryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(String guestName) {
        Optional<GuestListEntry> existingGuestOpt = guestListEntryRepository.findById(guestName);

        // Check if the guest exists, if not throw GuestNotFoundException
        if (existingGuestOpt.isEmpty()) {
            throw new GuestNotFoundException("Guest with name " + guestName + " not found");
        }

        var existingGuestEntry = existingGuestOpt.get();

        if (!existingGuestEntry.hasLeft()) {
            tableService.decreaseOccupancy(existingGuestEntry.getTableNumber(), existingGuestEntry.noOfGuests());
        }

        guestListEntryRepository.deleteById(guestName);
    }

    @Override
    @Transactional
    public GuestListEntry recordGuestArrival(String guestName, int accompanyingGuests) {
        // Check for negative accompanying guests
        if (accompanyingGuests < 0) {
            throw new IllegalArgumentException("Number of accompanying guests cannot be negative");
        }

        Optional<GuestListEntry> existingGuestOpt = guestListEntryRepository.findById(guestName);

        // Check if the guest exists
        if (existingGuestOpt.isEmpty()) {
            throw new GuestNotFoundException("Guest with name " + guestName + " not found");
        }

        var existingGuestEntry = existingGuestOpt.get();

        // Note: No check for whether the guest has already arrived.
        // This allows the number of accompanying guests to be changed even if the guest has already arrived.

        // Handle accompanying guests and table occupancy changes
        if (accompanyingGuests > existingGuestEntry.getAccompanyingGuests()) {
            int extraGuests = accompanyingGuests - existingGuestEntry.getAccompanyingGuests();

            if (tableService.hasAvailability(existingGuestEntry.getTableNumber(), extraGuests)) {
                tableService.increaseOccupancy(existingGuestEntry.getTableNumber(), extraGuests);
            } else {
                throw new NoAvailabilityException("Table " + existingGuestEntry.getTableNumber() + " does not have the required availability");
            }
        } else if (accompanyingGuests < existingGuestEntry.getAccompanyingGuests()) {
            int decreaseInGuests = existingGuestEntry.getAccompanyingGuests() - accompanyingGuests;
            tableService.decreaseOccupancy(existingGuestEntry.getTableNumber(), decreaseInGuests);
        }

        // Update guest entry with accompanying guests and, if not already recorded, the arrival time
        existingGuestEntry.setAccompanyingGuests(accompanyingGuests);
        if (!existingGuestEntry.hasArrived()) {
            existingGuestEntry.recordTimeArrived();
        }

        return guestListEntryRepository.save(existingGuestEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuestListEntry> getArrivedGuests() {
        return StreamSupport.stream(guestListEntryRepository.findAll().spliterator(), false)
                .filter(guestListEntry -> guestListEntry.getTimeArrived() != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GuestListEntry recordGuestLeft(String guestName) {
        Optional<GuestListEntry> existingGuestOpt = guestListEntryRepository.findById(guestName);

        // Check if the guest exists, if not throw GuestNotFoundException
        if (existingGuestOpt.isEmpty()) {
            throw new GuestNotFoundException("Guest with name " + guestName + " not found");
        }

        var existingGuestEntry = existingGuestOpt.get();

        // Check if guest has not been recorded as arrived yet
        if (!existingGuestEntry.hasArrived()) {
            throw new IllegalStateException("Guest with name " + guestName + " has not arrived yet, cannot record as left.");
        }

        // Check if guest has already been recorded as left
        if (existingGuestEntry.hasLeft()) {
            throw new IllegalStateException("Guest with name " + guestName + " has already been recorded as left.");
        }

        // Update the occupancy and record the time left
        tableService.increaseOccupancy(existingGuestEntry.getTableNumber(), existingGuestEntry.noOfGuests());
        existingGuestEntry.recordTimeLeft();
        return guestListEntryRepository.save(existingGuestEntry);
    }

    // Helper methods
    private void throwNoAvailabilityException(AddGuestRequest request) {
        if (request.hasTable()) {
            throw new NoAvailabilityException("Table " + request.getTable() + " does not have the required availability");
        } else {
            throw new NoAvailabilityException("No table was found with the required availability");
        }
    }

    private int getTableWithAvailability(int requestedTableNo, int noOfGuests) {
        if (requestedTableNo != 0 && tableService.hasAvailability(requestedTableNo, noOfGuests)) {
            return requestedTableNo;
        }
        return tableService.getTableWithAvailableSeating(noOfGuests);
    }
}
