package uk.co.imperatives.exercise.service;

import uk.co.imperatives.exercise.dto.GuestsAtTable;
import uk.co.imperatives.exercise.exception.GuestNotFoundException;
import uk.co.imperatives.exercise.exception.NameValidationError;
import uk.co.imperatives.exercise.exception.NoAvailabilityException;
import uk.co.imperatives.exercise.model.GuestListEntry;

import java.util.List;

public interface GuestListServiceInterface {

    GuestListEntry addGuest(AddGuestRequest request) throws NoAvailabilityException;

    GuestListEntry updateName(String oldName, String newName) throws NameValidationError, GuestNotFoundException;

    List<GuestListEntry> getAllGuests();

    void delete(String guestName) throws GuestNotFoundException;

    GuestListEntry recordGuestArrival(String guestName, int accompanyingGuests) throws GuestNotFoundException, NoAvailabilityException;

    List<GuestListEntry> getArrivedGuests();

    GuestListEntry recordGuestLeft(String guestName) throws GuestNotFoundException;

}
