package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.exception.TableAlreadyExistsException;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.repository.TableRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

public class TableServiceTests {
    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private TableService tableService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    @Test
    void shouldAddTableAndAssignTableNumber() {
        Table tableToAdd = Table.builder().noOfSeats(6).noOfSeatsAllocated(0).build();
        Table returnedTable = Table.builder().number(1).noOfSeats(6).noOfSeatsAllocated(0).version(0L).build();

        when(tableRepository.save(argThat(table ->
                table.getNumber() == null &&
                        table.getNoOfSeats() == tableToAdd.getNoOfSeats() &&
                        table.getNoOfSeatsAllocated() == tableToAdd.getNoOfSeatsAllocated() &&
                        table.getVersion() == null
        ))).thenReturn(returnedTable);

        // Call the service to add the table
        var noOfSeats = 6;
        Table result = tableService.addTable(noOfSeats);

        // Validate the result
        assertNotNull(result);
        assertEquals(returnedTable.getNumber(), result.getNumber());
        assertEquals(returnedTable.getNoOfSeats(), result.getNoOfSeats());
        assertEquals(returnedTable.getNoOfSeatsAllocated(), result.getNoOfSeatsAllocated());
        assertEquals(returnedTable.getVersion(), result.getVersion());
    }

    @Test
    void shouldAddTableWithGivenTableNumber() {

        Table tableToAdd = Table.builder().number(10).noOfSeats(4).noOfSeatsAllocated(0).build();
        Table returnedTable = Table.builder().number(10).noOfSeats(4).noOfSeatsAllocated(0).version(0L).build();

        when(tableRepository.existsById(10)).thenReturn(false);
        when(tableRepository.save(argThat(table ->
                table.getNumber().equals(tableToAdd.getNumber()) &&
                        table.getNoOfSeats() == tableToAdd.getNoOfSeats() &&
                        table.getNoOfSeatsAllocated() == tableToAdd.getNoOfSeatsAllocated() &&
                        table.getVersion() == null
        ))).thenReturn(returnedTable);

        var tableNumber = 10;
        var noOfSeats = 4;
        Table result = tableService.addTable(tableNumber, noOfSeats);

        // Validate the result
        assertNotNull(result);
        assertEquals(returnedTable.getNumber(), result.getNumber());
        assertEquals(returnedTable.getNoOfSeats(), result.getNoOfSeats());
        assertEquals(returnedTable.getNoOfSeatsAllocated(), result.getNoOfSeatsAllocated());
        assertEquals(returnedTable.getVersion(), result.getVersion());
    }

    @Test
    void shouldNotifyOnAddTableWhenSpecifiedTableAlreadyExists() {
        when(tableRepository.existsById(10)).thenReturn(true);

        TableAlreadyExistsException thrown = Assertions.assertThrows(TableAlreadyExistsException.class, () -> {
            var tableNumber = 10;
            var noOfSeats = 4;
            tableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Table with number 10 already exists.", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWhenSpecifiedTableIsZero() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = 0;
            var noOfSeats = 8;
            tableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Table number should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWhenSpecifiedTableIsNegative() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = -1;
            var noOfSeats = 8;
            tableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Table number should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableAndAssignTableNumberWhenNoOfSeatsIsNegative() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var noOfSeats = -1;
            tableService.addTable(noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableAndAssignTableNumberWhenNoOfSeatsIsZero() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var noOfSeats = 0;
            tableService.addTable(noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWithGivenTableNumberWhenNoOfSeatsIsZero() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = 1;
            var noOfSeats = 0;
            tableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldErrorOnAddTableWithGivenTableNumberWhenNoOfSeatsIsNegative() {

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            var tableNumber = 1;
            var noOfSeats = -1;
            tableService.addTable(tableNumber, noOfSeats);
        });

        Assertions.assertEquals("Number of seats should be a number bigger than zero", thrown.getMessage());
    }

    @Test
    void shouldReturnAllTables() {
        // Arrange: Set up tables in the repository
        List<Table> tablesInRepo = new ArrayList<>();
        tablesInRepo.add(Table.builder().number(1).noOfSeats(4).noOfSeatsAllocated(0).version(1L).build());
        tablesInRepo.add(Table.builder().number(2).noOfSeats(6).noOfSeatsAllocated(0).version(1L).build());
        tablesInRepo.add(Table.builder().number(3).noOfSeats(2).noOfSeatsAllocated(2).version(2L).build());

        // Mock the repository response
        when(tableRepository.findAll()).thenReturn(tablesInRepo);

        // Act: Call the service to get all tables
        List<Table> result = tableService.getAllTables();

        // Assert: Validate the result list
        assertNotNull(result);
        assertEquals(tablesInRepo.size(), result.size(), "The number of tables should match");

        // Compare the content of each table
        for (int i = 0; i < tablesInRepo.size(); i++) {
            Table expectedTable = tablesInRepo.get(i);
            Table actualTable = result.get(i);

            assertEquals(expectedTable.getNumber(), actualTable.getNumber(), "Table number should match");
            assertEquals(expectedTable.getNoOfSeats(), actualTable.getNoOfSeats(), "Number of seats should match");
            assertEquals(expectedTable.getNoOfSeatsAllocated(), actualTable.getNoOfSeatsAllocated(), "Seats allocated should match");
            assertEquals(expectedTable.getVersion(), actualTable.getVersion(), "Version should match");
        }
    }

}
