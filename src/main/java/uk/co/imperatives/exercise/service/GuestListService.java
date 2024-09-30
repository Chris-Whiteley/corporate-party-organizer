package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.imperatives.exercise.exception.GuestNotFoundException;
import uk.co.imperatives.exercise.exception.NoAvailabilityException;
import uk.co.imperatives.exercise.model.GuestListEntry;
import uk.co.imperatives.exercise.repository.GuestListEntryRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GuestListService {
    private final GuestListEntryRepository guestListEntryRepository;
    private final PartyTableServiceInterface tableService;

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
            // Temporarily remove places from the table
            tableService.decreaseOccupancy(existingGuest.getTableNumber(), existingGuest.noOfGuests());

            // Get a suitable table with availability
            var tableWithAvailability = getTableWithAvailability(request.getTable(), request.noOfGuests());
            if (tableWithAvailability == 0) {
                // No table with availability found, restore occupancy and throw exception
                tableService.increaseOccupancy(existingGuest.getTableNumber(), existingGuest.noOfGuests());
                throwNoAvailabilityException(request);
            }
            guestToAddBuilder.tableNumber(tableWithAvailability);
        } else {
            // Handle new guest case
            var tableWithAvailability = getTableWithAvailability(request.getTable(), request.noOfGuests());
            if (tableWithAvailability == 0) {
                throwNoAvailabilityException(request);
            }
            guestToAddBuilder.tableNumber(tableWithAvailability);
        }

        try {
            return guestListEntryRepository.save(guestToAddBuilder.build());
        } catch (OptimisticLockingFailureException e) {
            // handle the conflict, e.g., retry or notify the user
            return null;
        }
    }

    @Transactional
    public GuestListEntry updateName(String oldName, String newName) {
        // Find the guest by the old name
        Optional<GuestListEntry> existingGuestOpt = guestListEntryRepository.findById(oldName);

        // Check if the guest exists, if not throw GuestNotFoundException
        if (existingGuestOpt.isEmpty()) {
            throw new GuestNotFoundException("Guest with name " + oldName + " not found");
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
                .build();

        // Save the new guest
        guestListEntryRepository.save(updatedGuestListEntry);

        // Return the newly created guest
        return updatedGuestListEntry;
    }

    public List<GuestListEntry> getAllGuests() {
        return StreamSupport.stream(guestListEntryRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public GuestListEntry recordGuestArrival(String guestName, int accompanyingGuests) {
        Optional<GuestListEntry> existingGuestOpt = guestListEntryRepository.findById(guestName);

        // Check if the guest exists, if not throw GuestNotFoundException
        if (existingGuestOpt.isEmpty()) {
            throw new GuestNotFoundException("Guest with name " + guestName + " not found");
        }

        var existingGuestEntry = existingGuestOpt.get();
        existingGuestEntry.recordTimeArrived();
        return guestListEntryRepository.save(existingGuestEntry);
    }

    public void delete(String guestName) {
        if (!guestListEntryRepository.existsById(guestName)) {
            throw new GuestNotFoundException("Guest with name " + guestName + " not found");
        }

        guestListEntryRepository.deleteById(guestName);
    }

    public List<GuestListEntry> getArrivedGuests() {
        return StreamSupport.stream(guestListEntryRepository.findAll().spliterator(), false)
                .filter(guestListEntry -> guestListEntry.getTimeArrived() != null)
                .collect(Collectors.toList());
    }


    private void throwNoAvailabilityException(AddGuestRequest request) {
        if (request.hasTable()) {
            throw new NoAvailabilityException("Table " + request.getTable() + " does not have the required availability");
        } else {
            throw new NoAvailabilityException("No table was found with the required availability");
        }
    }

    private int getTableWithAvailability(int requestedTableNo, int noOfGuests) {
        if (requestedTableNo == 0) {
            return tableService.getTableWithAvailableSeating(noOfGuests);
        } else if (tableService.hasAvailability(requestedTableNo, noOfGuests)) {
            return requestedTableNo;
        }
        return 0; // No available table found
    }
}

