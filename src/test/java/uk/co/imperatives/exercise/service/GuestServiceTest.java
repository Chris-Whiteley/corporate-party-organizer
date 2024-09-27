package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.model.Guest;
import uk.co.imperatives.exercise.repository.GuestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GuestServiceTest {

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private TableServiceInterface tableService;

    @InjectMocks
    private GuestService guestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    @Test
    void shouldAddGuestToGuestList() {
        // table 1 has availability for 3
        when(tableService.hasAvailability(1, 3)).thenReturn(true);
        Guest guest = Guest.builder().name("John").table(1).accompanyingGuests(2).build();
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("John").table(1).accompanyingGuests(2).build();

        Guest result = guestService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(1, result.getTable());
        assertEquals(2, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestOnGuestList() {
        // table 2 has availability for 4
        when(tableService.hasAvailability(2, 4)).thenReturn(true);
        Guest existingGuest = Guest.builder().name("John").table(1).accompanyingGuests(2).build();
        Guest guest = Guest.builder().name("John").table(2).accompanyingGuests(3).build();
        when(guestRepository.findById("John")).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("John").table(2).accompanyingGuests(3).build();

        Guest result = guestService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals(2, result.getTable());
        assertEquals(3, result.getAccompanyingGuests());
    }

    @Test
    void shouldAddGuestToGuestListAndAssignAvailableTable() {
        // table 2 has availability for 6
        when(tableService.getTableWithAvailableSeating(anyInt())).thenReturn(2);
        Guest guest = Guest.builder().name("Elton John").table(2).accompanyingGuests(5).build();
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Elton John").accompanyingGuests(5).build();

        Guest result = guestService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("Elton John", result.getName());
        assertEquals(2, result.getTable());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestOnGuestListAndAssignAvailableTable() {
        // table 3 has availability for 6
        when(tableService.getTableWithAvailableSeating(6)).thenReturn(3);
        Guest existingGuest = Guest.builder().name("Elton John").table(2).accompanyingGuests(3).build();
        Guest guest = Guest.builder().name("Elton John").table(3).accompanyingGuests(5).build();
        when(guestRepository.findById("Elton John")).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Elton John").accompanyingGuests(5).build();

        Guest result = guestService.addGuest(request);

        assertNotNull(result);
        assertEquals("Elton John", result.getName());
        assertEquals(3, result.getTable());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestsName() {
        Guest existingGuest = Guest.builder().name("Cris Whitley").table(10).accompanyingGuests(5).build();
        Guest guest = Guest.builder().name("Chris Whiteley").table(10).accompanyingGuests(5).build();
        when(guestRepository.findById("Cris Whitley")).thenReturn(Optional.of(existingGuest));
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        Guest result = guestService.updateName("Cris Whitley", "Chris Whiteley");

        assertNotNull(result);
        assertEquals("Chris Whiteley", result.getName());
        assertEquals(10, result.getTable());
        assertEquals(5, result.getAccompanyingGuests());
    }


}
