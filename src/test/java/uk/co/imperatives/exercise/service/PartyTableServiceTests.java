package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.model.PartyTable;
import uk.co.imperatives.exercise.repository.PartyTableRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

public class PartyTableServiceTests {
    @Mock
    private PartyTableRepository partyTableRepository;

    @InjectMocks
    private PartyTableService partyTableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    @Test
    void shouldAddTableAndAssignTableNumber() {
        PartyTable returnedTable = PartyTable.builder().number(2).noOfSeats(6).noOfSeatsAllocated(0).build();

        when(partyTableRepository.existsById(1)).thenReturn(true);
        when(partyTableRepository.existsById(2)).thenReturn(false);

        when(partyTableRepository.save(argThat(table ->
                table.getNumber().equals(returnedTable.getNumber()) &&
                        table.getNoOfSeats() == returnedTable.getNoOfSeats() &&
                        table.getNoOfSeatsAllocated() == returnedTable.getNoOfSeatsAllocated() &&
                        table.getVersion().equals(returnedTable.getVersion())
        ))).thenReturn(returnedTable);

        // Call the service to add the table
        var noOfSeats = 6;
        PartyTable result = partyTableService.addTable(noOfSeats);

        // Validate the result
        assertNotNull(result);
        assertEquals(returnedTable.getNumber(), result.getNumber());
        assertEquals(returnedTable.getNoOfSeats(), result.getNoOfSeats());
        assertEquals(returnedTable.getNoOfSeatsAllocated(), result.getNoOfSeatsAllocated());
        assertEquals(returnedTable.getVersion(), result.getVersion());
    }

    @Test
    void shouldAddTableWithGivenTableNumber() {

        PartyTable tableToAdd = PartyTable.builder().number(10).noOfSeats(4).noOfSeatsAllocated(0).build();
        PartyTable returnedTable = PartyTable.builder().number(10).noOfSeats(4).noOfSeatsAllocated(0).version(0L).build();

        when(partyTableRepository.existsById(10)).thenReturn(false);
        when(partyTableRepository.save(argThat(table ->
                table.getNumber().equals(tableToAdd.getNumber()) &&
                        table.getNoOfSeats() == tableToAdd.getNoOfSeats() &&
                        table.getNoOfSeatsAllocated() == tableToAdd.getNoOfSeatsAllocated() &&
                        table.getVersion().equals(tableToAdd.getVersion())
        ))).thenReturn(returnedTable);

        var tableNumber = 10;
        var noOfSeats = 4;
        PartyTable result = partyTableService.addTable(tableNumber, noOfSeats);

        // Validate the result
        assertNotNull(result);
        assertEquals(returnedTable.getNumber(), result.getNumber());
        assertEquals(returnedTable.getNoOfSeats(), result.getNoOfSeats());
        assertEquals(returnedTable.getNoOfSeatsAllocated(), result.getNoOfSeatsAllocated());
        assertEquals(returnedTable.getVersion(), result.getVersion());
    }

    @Test
    void shouldNotifyOnAddTableWhenSpecifiedTableAlreadyExists() {
        when(partyTableRepository.existsById(10)).thenReturn(true);

        TableAlreadyExistsException thrown = Assertions.assertThrows(TableAlreadyExistsException.class, () -> {
            var tableNumber = 10;
            var noOfSeats = 4;
            partyTableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Table with number 10 already exists.", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWhenSpecifiedTableIsZero() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = 0;
            var noOfSeats = 8;
            partyTableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Table number should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWhenSpecifiedTableIsNegative() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = -1;
            var noOfSeats = 8;
            partyTableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Table number should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableAndAssignTableNumberWhenNoOfSeatsIsNegative() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var noOfSeats = -1;
            partyTableService.addTable(noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableAndAssignTableNumberWhenNoOfSeatsIsZero() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var noOfSeats = 0;
            partyTableService.addTable(noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWithGivenTableNumberWhenNoOfSeatsIsZero() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = 1;
            var noOfSeats = 0;
            partyTableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWithGivenTableNumberWhenNoOfSeatsIsNegative() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = 1;
            var noOfSeats = -1;
            partyTableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldReturnAllTables() {
        // Arrange: Set up tables in the repository
        List<PartyTable> tablesInRepo = new ArrayList<>();
        tablesInRepo.add(PartyTable.builder().number(1).noOfSeats(4).noOfSeatsAllocated(0).version(1L).build());
        tablesInRepo.add(PartyTable.builder().number(2).noOfSeats(6).noOfSeatsAllocated(0).version(1L).build());
        tablesInRepo.add(PartyTable.builder().number(3).noOfSeats(2).noOfSeatsAllocated(2).version(2L).build());

        // Mock the repository response
        when(partyTableRepository.findAll()).thenReturn(tablesInRepo);

        // Act: Call the service to get all tables
        List<PartyTable> result = partyTableService.getAllTables();

        // Assert: Validate the result list
        assertNotNull(result);
        assertEquals(tablesInRepo.size(), result.size(), "The number of tables should match");

        // Compare the content of each table
        for (int i = 0; i < tablesInRepo.size(); i++) {
            PartyTable expectedTable = tablesInRepo.get(i);
            PartyTable actualTable = result.get(i);

            assertEquals(expectedTable.getNumber(), actualTable.getNumber(), "Table number should match");
            assertEquals(expectedTable.getNoOfSeats(), actualTable.getNoOfSeats(), "Number of seats should match");
            assertEquals(expectedTable.getNoOfSeatsAllocated(), actualTable.getNoOfSeatsAllocated(), "Seats allocated should match");
            assertEquals(expectedTable.getVersion(), actualTable.getVersion(), "Version should match");
        }
    }
}
