package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.imperatives.exercise.exception.TableNotFoundException;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.repository.GuestRepository;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final TableServiceInterface tableService;

    @Transactional
    public Guest addGuest(AddGuestRequest request) {
        // Check if the guest already exists
        var existingGuestOptional = guestRepository.findById(request.getName());

        // Build the guest to add
        var guestToAddBuilder = Guest.builder()
                .name(request.getName())
                .accompanyingGuests(request.getAccompanyingGuests());

        // Handle existing guest case
        if (existingGuestOptional.isPresent()) {
            var existingGuest = existingGuestOptional.get();
            // Temporarily remove places from the table
            tableService.decreaseOccupancy(existingGuest.getTable(), existingGuest.noOfGuests());

            // Get a suitable table with availability
            var tableWithAvailability = getTableWithAvailability(request.getTable(), request.noOfGuests());
            if (tableWithAvailability == 0) {
                // No table with availability found, restore occupancy and throw exception
                tableService.increaseOccupancy(existingGuest.getTable(), existingGuest.noOfGuests());
                throw new TableNotFoundException("Table with availability not found for request " + request);
            }
            guestToAddBuilder.table(tableWithAvailability);
        } else {
            // Handle new guest case
            var tableWithAvailability = getTableWithAvailability(request.getTable(), request.noOfGuests());
            if (tableWithAvailability == 0) {
                throw new TableNotFoundException("Table with availability not found for request " + request);
            }
            guestToAddBuilder.table(tableWithAvailability);
        }


        try {
            return guestRepository.save(guestToAddBuilder.build());
        } catch (OptimisticLockingFailureException e) {
            // handle the conflict, e.g., retry or notify the user
            return null;
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

