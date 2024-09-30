package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.exception.NoAvailabilityException;
import uk.co.imperatives.exercise.model.GuestListEntry;
import uk.co.imperatives.exercise.repository.GuestListEntryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GuestListEntryServiceTests {

    @Mock
    private GuestListEntryRepository guestListEntryRepository;

    @Mock
    private TableServiceInterface tableService;

    @InjectMocks
    private GuestListService guestListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    @Test
    void shouldAddGuestToGuestList() {
        // table 1 has availability for 3
        when(tableService.hasAvailability(1, 3)).thenReturn(true);
        GuestListEntry guestListEntry = GuestListEntry.builder().name("John").table(1).accompanyingGuests(2).build();
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("John").table(1).accompanyingGuests(2).build();

        GuestListEntry result = guestListService.addGuest(request);

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
        GuestListEntry existingGuestListEntry = GuestListEntry.builder().name("John").table(1).accompanyingGuests(2).build();
        GuestListEntry guestListEntry = GuestListEntry.builder().name("John").table(2).accompanyingGuests(3).build();
        when(guestListEntryRepository.findById("John")).thenReturn(Optional.of(existingGuestListEntry));
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("John").table(2).accompanyingGuests(3).build();

        GuestListEntry result = guestListService.addGuest(request);

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
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Elton John").table(2).accompanyingGuests(5).build();
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Elton John").accompanyingGuests(5).build();

        GuestListEntry result = guestListService.addGuest(request);

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
        GuestListEntry existingGuestListEntry = GuestListEntry.builder().name("Elton John").table(2).accompanyingGuests(3).build();
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Elton John").table(3).accompanyingGuests(5).build();
        when(guestListEntryRepository.findById("Elton John")).thenReturn(Optional.of(existingGuestListEntry));
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Elton John").accompanyingGuests(5).build();

        GuestListEntry result = guestListService.addGuest(request);

        assertNotNull(result);
        assertEquals("Elton John", result.getName());
        assertEquals(3, result.getTable());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestsName() {
        GuestListEntry existingGuestListEntry = GuestListEntry.builder().name("Cris Whitley").table(10).accompanyingGuests(5).build();
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Chris Whiteley").table(10).accompanyingGuests(5).build();
        when(guestListEntryRepository.findById("Cris Whitley")).thenReturn(Optional.of(existingGuestListEntry));
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        GuestListEntry result = guestListService.updateName("Cris Whitley", "Chris Whiteley");

        assertNotNull(result);
        assertEquals("Chris Whiteley", result.getName());
        assertEquals(10, result.getTable());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldNotifyWhenSpecifiedTableDoesNotHaveTheAvailability() {
        when(tableService.hasAvailability(2, 4)).thenReturn(false);

        NoAvailabilityException thrown = Assertions.assertThrows(NoAvailabilityException.class, () -> {
            // Build request and call service
            AddGuestRequest request = AddGuestRequest.builder().name("John").table(2).accompanyingGuests(3).build();
            guestListService.addGuest(request);
        });

        Assertions.assertEquals("Table 2 does not have the required availability", thrown.getMessage());
    }

    @Test
    void shouldNotifyWhenNoTableHasTheAvailability() {
        when(tableService.getTableWithAvailableSeating(6)).thenReturn(0);

        NoAvailabilityException thrown = Assertions.assertThrows(NoAvailabilityException.class, () -> {
            // Build request and call service
            AddGuestRequest request = AddGuestRequest.builder().name("Chris").accompanyingGuests(5).build();
            guestListService.addGuest(request);
        });

        Assertions.assertEquals("No table was found with the required availability", thrown.getMessage());
    }
}
