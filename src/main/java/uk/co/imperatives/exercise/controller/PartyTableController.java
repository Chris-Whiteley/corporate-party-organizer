package uk.co.imperatives.exercise.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.imperatives.exercise.dto.AddTableRequest;
import uk.co.imperatives.exercise.dto.PartyTableDto;
import uk.co.imperatives.exercise.model.PartyTable;
import uk.co.imperatives.exercise.service.PartyTableService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/party_tables")
@RequiredArgsConstructor
public class PartyTableController {

    private final PartyTableService partyTableService;


    @Operation(summary = "Add a new table", description = "Creates a new party table. Requires the number of seats, " +
            "and optionally the table number.  If no table number is provided the system will assign an available number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Table successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PartyTableDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input: Missing required field noOfSeats",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Table already exists",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<PartyTableDto> addTable(@RequestBody AddTableRequest request) {
        PartyTable createdTable;

        if (request.getTableNumber() != null && request.getNoOfSeats() != null) {
            createdTable = partyTableService.addTable(request.getTableNumber(), request.getNoOfSeats());
        } else if (request.getNoOfSeats() != null) {
            createdTable = partyTableService.addTable(request.getNoOfSeats());
        } else {
            throw new IllegalArgumentException("Required argument noOfSeats is null");
        }

        return new ResponseEntity<>(
                PartyTableDto.builder()
                        .number(createdTable.getNumber())
                        .noOfSeats(createdTable.getNoOfSeats())
                        .noOfSeatsAllocated(createdTable.getNoOfSeatsAllocated())
                        .build(), HttpStatus.CREATED);
    }




    @Operation(summary = "Get all tables", description = "Fetches all the tables in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tables",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PartyTableDto.class))),
            @ApiResponse(responseCode = "404", description = "No tables found",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<PartyTableDto>> getAllTables() {
        List<PartyTable> tables = partyTableService.getAllTables();

        // Convert PartyTable to PartyTableDto
        List<PartyTableDto> tableDtos = tables.stream()
                .map(table -> PartyTableDto.builder()
                        .number(table.getNumber())
                        .noOfSeats(table.getNoOfSeats())
                        .noOfSeatsAllocated(table.getNoOfSeatsAllocated())
                        .build())
                .collect(Collectors.toList());

        return new ResponseEntity<>(tableDtos, HttpStatus.OK);
    }

    @Operation(summary = "Remove a party table", description = "Deletes a party table by its number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the table"),
            @ApiResponse(responseCode = "400", description = "Invalid table number supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Table not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Table cannot be deleted because it has allocated seats",
                    content = @Content)
    })
    @DeleteMapping("/{tableNumber}")
    public ResponseEntity<Void> removeTable(@PathVariable int tableNumber) {
        partyTableService.removeTable(tableNumber);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
