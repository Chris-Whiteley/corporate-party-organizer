package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.imperatives.exercise.model.Table;
import uk.co.imperatives.exercise.repository.TableRepository;

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
                table.getNumber() == null  &&
                        table.getNoOfSeats() == tableToAdd.getNoOfSeats() &&
                        table.getNoOfSeatsAllocated() == tableToAdd.getNoOfSeatsAllocated() &&
                        table.getVersion()== null // matching null version field
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

}
