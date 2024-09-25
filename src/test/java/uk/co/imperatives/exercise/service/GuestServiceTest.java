package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.model.Guest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GuestServiceTest {

    @InjectMocks
    private GuestService guestService; // Injects mock dependencies

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    @Test
    void shouldAddGuestToGuestList() {

        // Build request and call service
        AddGuestRequest request =
                AddGuestRequest.builder().name("John").table(1).accompanyingGuests(2).build();

        Guest result = guestService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(1, result.getTable());
        assertEquals(2, result.getAccompanyingGuests());

    }
}
