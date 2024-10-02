package uk.co.imperatives.exercise.controller;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class GuestListController {

    private final GuestListServiceInterface guestListService;
    private final PartyTableServiceInterface partyTableService;


    // Add a new guest
    @PostMapping("/guest_list")
    public ResponseEntity<GuestListEntryDto> addGuest(@RequestBody AddGuestRequestDto request) {
        GuestListEntry addedGuest = guestListService.addGuest(toAddGuestRequest(request));
        return new ResponseEntity<>(toDto(addedGuest), HttpStatus.CREATED);
    }

    // Update a guest's name
    @PutMapping("/guest_list/{oldName}/name")
    public ResponseEntity<GuestListEntryDto> updateGuestName(
            @PathVariable String oldName,
            @RequestParam String newName) {
        GuestListEntry updatedGuest = guestListService.updateName(oldName, newName);
        return ResponseEntity.ok(toDto(updatedGuest));
    }

    // Get all guests
    @GetMapping("/guest_list")
    public ResponseEntity<List<GuestListEntryDto>> getAllGuests() {
        List<GuestListEntryDto> guestList =
                guestListService.getAllGuests()
                        .stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(guestList);
    }

    // Delete a guest from the guestList
    @DeleteMapping("/guest_list/{guestName}")
    public ResponseEntity<Void> deleteGuest(@PathVariable String guestName) {
        guestListService.delete(guestName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/guests")
    public ResponseEntity<GuestListEntryDto> recordGuestArrival(@RequestBody AddGuestRequestDto request) {
        // Check if the guest's table has enough space for additional guests
        GuestListEntry updatedGuest = guestListService.recordGuestArrival(request.getName(), request.getAccompanyingGuests());
        return ResponseEntity.ok(toDto(updatedGuest));
    }

    // Record guest leaving
    @DeleteMapping("/guests/{guestName}")
    public ResponseEntity<GuestListEntryDto> recordGuestLeaving(@PathVariable String guestName) {
        // Check if the guest's table has enough space for additional guests
        GuestListEntry updatedGuest = guestListService.recordGuestLeft(guestName);
        return ResponseEntity.ok(toDto(updatedGuest));
    }

    // Get guests at all tables
    @GetMapping("/guests_at_table")
    public ResponseEntity<List<GuestsAtTable>> getGuestsAtAllTables() {
        List<GuestsAtTable> guestsAtTables = guestListService.getGuestsAtAllTables();
        return ResponseEntity.ok(guestsAtTables);
    }

    // Get guests at a specific table
    @GetMapping("/guests_at_table/{tableNumber}")
    public ResponseEntity<GuestsAtTable> getGuestsAtTable(@PathVariable int tableNumber) {
        GuestsAtTable guestsAtTable = guestListService.getGuestsAtTable(tableNumber);
        return ResponseEntity.ok(guestsAtTable);
    }

    @GetMapping("/guests")
    public ResponseEntity<List<GuestListEntryDto>> getArrivedGuests() {
        List<GuestListEntry> arrivedGuests = guestListService.getArrivedGuests();
        List<GuestListEntryDto> arrivedGuestDtos = arrivedGuests.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(arrivedGuestDtos);
    }

    @GetMapping("/seats_empty")
    public ResponseEntity<Map<String, Integer>> getEmptySeats() {
        // Prepare the response body as a JSON object
        Map<String, Integer> response = new HashMap<>();
        response.put("seats_empty", partyTableService.getTotalEmptySeats());
        return ResponseEntity.ok(response);
    }

    private GuestListEntryDto toDto(GuestListEntry guestListEntry) {
        return GuestListEntryDto.builder()
                .name(guestListEntry.getName())
                .tableNumber(guestListEntry.getTableNumber())
                .timeArrived(guestListEntry.getTimeArrived())
                .accompanyingGuests(guestListEntry.getAccompanyingGuests())
                .build();
    }

    private AddGuestRequest toAddGuestRequest (AddGuestRequestDto dto) {
        return AddGuestRequest
                .builder()
                .name(dto.getName())
                .table(dto.getTable())
                .accompanyingGuests(dto.getAccompanyingGuests())
                .build();
    }

}
