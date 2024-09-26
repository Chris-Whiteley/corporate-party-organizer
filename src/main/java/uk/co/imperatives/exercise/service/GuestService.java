package uk.co.imperatives.exercise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.repository.GuestRepository;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final TableService tableService;

    public Guest addGuest(AddGuestRequest request) {
        var guest = Guest.builder()
                        .name(request.getName())
                        .accompanyingGuests(request.getAccompanyingGuests())
                        .table(request.getTable())
                        .build();

        return guestRepository.save(guest);
    }
}
