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
    @Mock
    private TableService tableService;

    @InjectMocks
    private GuestService guestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddGuestToGuestList() {
        // table 1 has availability for 3
        when(tableService.hasAvailability(1,3)).thenReturn(true);
        Guest guest = Guest.builder().name("John").table(1).accompanyingGuests(2).build();
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
    }

    @Test
    void shouldAddGuestToGuestListAndAssignAvailableTable() {
        // table 2 has availability for 6
        when(tableService.getTableWithAvailableSeating(anyInt())).thenReturn(2);
        Guest guest = Guest.builder().name("Elton John").table(2).accompanyingGuests(5).build();
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Build request and call service
        AddGuestRequest request =
                AddGuestRequest.builder().name("Elton John").accompanyingGuests(5).build();

        Guest result = guestService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("Elton John", result.getName());
        assertEquals(2, result.getTable());
        assertEquals(5, result.getAccompanyingGuests());
    }


}
