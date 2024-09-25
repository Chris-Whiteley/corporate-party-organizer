package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.repository.GuestRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @InjectMocks
    private GuestService guestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddGuestToGuestList() {
        // table 1 has capacity for 3
        Guest guest = new Guest("John", 1, 2);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Build request and call service
        AddGuestRequest request =
                AddGuestRequest.builder().name("John").table(1).accompanyingGuests(2).build();

        Guest result = guestService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(1, result.getTable());
        assertEquals(2, result.getAccompanyingGuests());

        // Verify repository interaction
        verify(guestRepository, times(1)).save(any(Guest.class));
    }
}
