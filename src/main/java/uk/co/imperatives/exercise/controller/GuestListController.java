package uk.co.imperatives.exercise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.imperatives.exercise.dto.AddGuestRequestDto;
import uk.co.imperatives.exercise.dto.GuestListEntryDto;
import uk.co.imperatives.exercise.dto.GuestsAtTable;
import uk.co.imperatives.exercise.model.GuestListEntry;
import uk.co.imperatives.exercise.service.AddGuestRequest;
import uk.co.imperatives.exercise.service.GuestListServiceInterface;
import uk.co.imperatives.exercise.service.PartyTableServiceInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Guest List", description = "APIs to manage guest list and tables at the party")
public class GuestListController {

    private final GuestListServiceInterface guestListService;
    private final PartyTableServiceInterface partyTableService;

    @Operation(summary = "Add a new guest", description = "Registers a new guest and assigns them to a table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Guest added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Guest already exists")
    })
    @PostMapping("/guest_list")
    public ResponseEntity<GuestListEntryDto> addGuest(@RequestBody AddGuestRequestDto request) {
        GuestListEntry addedGuest = guestListService.addGuest(toAddGuestRequest(request));
        return new ResponseEntity<>(toDto(addedGuest), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a guest's name", description = "Updates the name of an existing guest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest name updated successfully"),
            @ApiResponse(responseCode = "404", description = "Guest not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/guest_list/{oldName}/name")
    public ResponseEntity<GuestListEntryDto> updateGuestName(
            @Parameter(description = "The current name of the guest") @PathVariable String oldName,
            @Parameter(description = "The new name to update") @RequestParam String newName) {
        GuestListEntry updatedGuest = guestListService.updateName(oldName, newName);
        return ResponseEntity.ok(toDto(updatedGuest));
    }

    @Operation(summary = "Get all guests", description = "Fetches the entire guest list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of guests retrieved successfully")
    })
    @GetMapping("/guest_list")
    public ResponseEntity<List<GuestListEntryDto>> getAllGuests() {
        List<GuestListEntryDto> guestList =
                guestListService.getAllGuests()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(guestList);
    }

    @Operation(summary = "Delete a guest", description = "Removes a guest from the guest list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Guest deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Guest not found")
    })
    @DeleteMapping("/guest_list/{guestName}")
    public ResponseEntity<Void> deleteGuest(@Parameter(description = "The name of the guest to be deleted") @PathVariable String guestName) {
        guestListService.delete(guestName);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Record guest arrival", description = "Marks the guest as having arrived.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest arrival recorded"),
            @ApiResponse(responseCode = "404", description = "Guest not found")
    })
    @PutMapping("/guests")
    public ResponseEntity<GuestListEntryDto> recordGuestArrival(@RequestBody AddGuestRequestDto request) {
        GuestListEntry updatedGuest = guestListService.recordGuestArrival(request.getName(), request.getAccompanyingGuests());
        return ResponseEntity.ok(toDto(updatedGuest));
    }

    @Operation(summary = "Record guest leaving", description = "Marks the guest as having left the party.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest leaving recorded"),
            @ApiResponse(responseCode = "404", description = "Guest not found")
    })
    @DeleteMapping("/guests/{guestName}")
    public ResponseEntity<GuestListEntryDto> recordGuestLeaving(@Parameter(description = "The name of the guest leaving") @PathVariable String guestName) {
        GuestListEntry updatedGuest = guestListService.recordGuestLeft(guestName);
        return ResponseEntity.ok(toDto(updatedGuest));
    }

    @Operation(summary = "Get guests at all tables", description = "Retrieves a list of all guests at their respective tables.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of guests at tables retrieved successfully")
    })
    @GetMapping("/guests_at_table")
    public ResponseEntity<List<GuestsAtTable>> getGuestsAtAllTables() {
        List<GuestsAtTable> guestsAtTables = guestListService.getGuestsAtAllTables();
        return ResponseEntity.ok(guestsAtTables);
    }

    @Operation(summary = "Get guests at a specific table", description = "Retrieves the guests seated at a specific table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guests at table retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Table not found")
    })
    @GetMapping("/guests_at_table/{tableNumber}")
    public ResponseEntity<GuestsAtTable> getGuestsAtTable(
            @Parameter(description = "The number of the table to retrieve guests from") @PathVariable int tableNumber) {
        GuestsAtTable guestsAtTable = guestListService.getGuestsAtTable(tableNumber);
        return ResponseEntity.ok(guestsAtTable);
    }

    @Operation(summary = "Get guests who have arrived", description = "Retrieves a list of guests who have already arrived at the party.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of arrived guests retrieved successfully")
    })
    @GetMapping("/guests")
    public ResponseEntity<List<GuestListEntryDto>> getArrivedGuests() {
        List<GuestListEntry> arrivedGuests = guestListService.getArrivedGuests();
        List<GuestListEntryDto> arrivedGuestDtos = arrivedGuests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(arrivedGuestDtos);
    }

    @Operation(summary = "Get empty seats", description = "Retrieves the total number of empty seats at the party.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Number of empty seats retrieved successfully")
    })
    @GetMapping("/seats_empty")
    public ResponseEntity<Map<String, Integer>> getEmptySeats() {
        Map<String, Integer> response = new HashMap<>();
        response.put("seats_empty", partyTableService.getTotalEmptySeats());
        return ResponseEntity.ok(response);
    }

    private GuestListEntryDto toDto(GuestListEntry guestListEntry) {
        return GuestListEntryDto.builder()
                .name(guestListEntry.getName())
                .tableNumber(guestListEntry.getTableNumber())
                .timeArrived(formatLocalDateTime(guestListEntry.getTimeArrived()))
                .timeLeft(formatLocalDateTime(guestListEntry.getTimeLeft()))
                .accompanyingGuests(guestListEntry.getAccompanyingGuests())
                .build();
    }

    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        }
        return "";
    }

    private AddGuestRequest toAddGuestRequest(AddGuestRequestDto dto) {
        return AddGuestRequest
                .builder()
                .name(dto.getName())
                .table(dto.getTable())
                .accompanyingGuests(dto.getAccompanyingGuests())
                .build();
    }
}
