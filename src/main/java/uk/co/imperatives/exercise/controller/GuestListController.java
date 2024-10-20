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
import uk.co.imperatives.exercise.dto.GuestArrivalDto;
import uk.co.imperatives.exercise.dto.GuestListEntryDto;
import uk.co.imperatives.exercise.model.GuestListEntry;
import uk.co.imperatives.exercise.service.AddGuestRequest;
import uk.co.imperatives.exercise.service.GuestListServiceInterface;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/guest_list")
@RequiredArgsConstructor
@Tag(name = "Guest List", description = "APIs to manage guest list and tables at the party")
public class GuestListController {

    private final GuestListServiceInterface guestListService;

    @Operation(summary = "Add a new guest", description = "Registers a new guest and assigns them to a table." +
            " If Supplied table is 0 the system will attempt to find an available table.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Guest added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Guest already exists")
    })
    @PostMapping
    public ResponseEntity<GuestListEntryDto> addGuest(@RequestBody AddGuestRequestDto request) {
        GuestListEntry addedGuest = guestListService.addGuest(toAddGuestRequest(request));
        return new ResponseEntity<>(GuestListEntryDto.toDto(addedGuest), HttpStatus.CREATED);
    }

    @Operation(summary = "Update a guest's name", description = "Updates the name of an existing guest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest name updated successfully"),
            @ApiResponse(responseCode = "404", description = "Guest not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{oldName}/name")
    public ResponseEntity<GuestListEntryDto> updateGuestName(
            @Parameter(description = "The current name of the guest") @PathVariable String oldName,
            @Parameter(description = "The new name to update") @RequestParam String newName) {
        GuestListEntry updatedGuest = guestListService.updateName(oldName, newName);
        return ResponseEntity.ok(GuestListEntryDto.toDto(updatedGuest));
    }

    @Operation(summary = "Get all guests", description = "Fetches the entire guest list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of guests retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<GuestListEntryDto>> getAllGuests() {
        List<GuestListEntryDto> guestList =
                guestListService.getAllGuests()
                        .stream()
                        .map(GuestListEntryDto::toDto)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(guestList);
    }

    @Operation(summary = "Delete a guest", description = "Removes a guest from the guest list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Guest deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Guest not found")
    })
    @DeleteMapping("/{guestName}")
    public ResponseEntity<Void> deleteGuest(@Parameter(description = "The name of the guest to be deleted") @PathVariable String guestName) {
        guestListService.delete(guestName);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Record guest arrival", description = "Marks the guest as having arrived, and updates the number of accompanying guests if necessary. The number of accompanying guests must not be negative.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest arrival recorded"),
            @ApiResponse(responseCode = "400", description = "Invalid input: accompanying guests cannot be negative"),
            @ApiResponse(responseCode = "404", description = "Guest not found")
    })
    @PutMapping("/arrive")
    public ResponseEntity<GuestListEntryDto> recordGuestArrival(@RequestBody GuestArrivalDto request) {
        GuestListEntry updatedGuest = guestListService.recordGuestArrival(request.getName(), request.getAccompanyingGuests());
        return ResponseEntity.ok(GuestListEntryDto.toDto(updatedGuest));
    }


    @Operation(summary = "Record guest leaving", description = "Marks the guest as having left the party. A guest must have been recorded as arrived before being marked as left.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Guest leaving recorded"),
            @ApiResponse(responseCode = "404", description = "Guest not found"),
            @ApiResponse(responseCode = "409", description = "Guest has not arrived or has already left")
    })
    @PatchMapping("/{guestName}/leave")
    public ResponseEntity<GuestListEntryDto> recordGuestLeaving(
            @Parameter(description = "The name of the guest leaving") @PathVariable String guestName) {
        GuestListEntry updatedGuest = guestListService.recordGuestLeft(guestName);
        return ResponseEntity.ok(GuestListEntryDto.toDto(updatedGuest));
    }

    @Operation(summary = "Get guests who have arrived", description = "Retrieves a list of guests who have already arrived at the party.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of arrived guests retrieved successfully")
    })
    @GetMapping("/arrived")
    public ResponseEntity<List<GuestListEntryDto>> getArrivedGuests() {
        List<GuestListEntry> arrivedGuests = guestListService.getArrivedGuests();
        List<GuestListEntryDto> arrivedGuestDtos = arrivedGuests.stream()
                .map(GuestListEntryDto::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(arrivedGuestDtos);
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
