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

public class GuestListServiceTests {

    @Mock
    private GuestListEntryRepository guestListEntryRepository;

    @Mock
    private PartyTableServiceInterface tableService;

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
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Mickey Mouse").tableNumber(1).accompanyingGuests(2).build();
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Mickey Mouse").table(1).accompanyingGuests(2).build();

        GuestListEntry result = guestListService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("Mickey Mouse", result.getName());
        assertEquals(1, result.getTableNumber());
        assertEquals(2, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestOnGuestList() {
        // table 2 has availability for 4
        when(tableService.hasAvailability(2, 4)).thenReturn(true);
        GuestListEntry existingGuestListEntry = GuestListEntry.builder().name("Donald Duck").tableNumber(1).accompanyingGuests(2).build();
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Donald Duck").tableNumber(2).accompanyingGuests(3).build();
        when(guestListEntryRepository.findById("Donald Duck")).thenReturn(Optional.of(existingGuestListEntry));
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Donald Duck").table(2).accompanyingGuests(3).build();

        GuestListEntry result = guestListService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("Donald Duck", result.getName());
        assertEquals(2, result.getTableNumber());
        assertEquals(3, result.getAccompanyingGuests());
    }

    @Test
    void shouldAddGuestToGuestListAndAssignAvailableTable() {
        // table 2 has availability for 6
        when(tableService.getTableWithAvailableSeating(anyInt())).thenReturn(2);
        GuestListEntry guestListEntry = GuestListEntry.builder().name("SpongeBob SquarePants").tableNumber(2).accompanyingGuests(5).build();
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("SpongeBob SquarePants").accompanyingGuests(5).build();

        GuestListEntry result = guestListService.addGuest(request);

        // Validate the result
        assertNotNull(result);
        assertEquals("SpongeBob SquarePants", result.getName());
        assertEquals(2, result.getTableNumber());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestOnGuestListAndAssignAvailableTable() {
        // table 3 has availability for 6
        when(tableService.getTableWithAvailableSeating(6)).thenReturn(3);
        GuestListEntry existingGuestListEntry = GuestListEntry.builder().name("Patrick Star").tableNumber(2).accompanyingGuests(3).build();
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Patrick Star").tableNumber(3).accompanyingGuests(5).build();
        when(guestListEntryRepository.findById("Patrick Star")).thenReturn(Optional.of(existingGuestListEntry));
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        // Build request and call service
        AddGuestRequest request = AddGuestRequest.builder().name("Patrick Star").accompanyingGuests(5).build();

        GuestListEntry result = guestListService.addGuest(request);

        assertNotNull(result);
        assertEquals("Patrick Star", result.getName());
        assertEquals(3, result.getTableNumber());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldUpdateExistingGuestsName() {
        GuestListEntry existingGuestListEntry = GuestListEntry.builder().name("Tom Cat").tableNumber(10).accompanyingGuests(5).build();
        GuestListEntry guestListEntry = GuestListEntry.builder().name("Tommy Cat").tableNumber(10).accompanyingGuests(5).build();
        when(guestListEntryRepository.findById("Tom Cat")).thenReturn(Optional.of(existingGuestListEntry));
        when(guestListEntryRepository.save(any(GuestListEntry.class))).thenReturn(guestListEntry);

        GuestListEntry result = guestListService.updateName("Tom Cat", "Tommy Cat");

        assertNotNull(result);
        assertEquals("Tommy Cat", result.getName());
        assertEquals(10, result.getTableNumber());
        assertEquals(5, result.getAccompanyingGuests());
    }

    @Test
    void shouldNotifyWhenSpecifiedTableDoesNotHaveTheAvailability() {
        when(tableService.hasAvailability(2, 4)).thenReturn(false);

        NoAvailabilityException thrown = Assertions.assertThrows(NoAvailabilityException.class, () -> {
            // Build request and call service
            AddGuestRequest request = AddGuestRequest.builder().name("Bugs Bunny").table(2).accompanyingGuests(3).build();
            guestListService.addGuest(request);
        });

        Assertions.assertEquals("Table 2 does not have the required availability", thrown.getMessage());
    }

    @Test
    void shouldNotifyWhenNoTableHasTheAvailability() {
        when(tableService.getTableWithAvailableSeating(6)).thenReturn(0);

        NoAvailabilityException thrown = Assertions.assertThrows(NoAvailabilityException.class, () -> {
            // Build request and call service
            AddGuestRequest request = AddGuestRequest.builder().name("Sylvester the Cat").accompanyingGuests(5).build();
            guestListService.addGuest(request);
        });

        Assertions.assertEquals("No table was found with the required availability", thrown.getMessage());
    }
}
