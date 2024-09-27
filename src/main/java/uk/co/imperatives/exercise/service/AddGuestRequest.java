package uk.co.imperatives.exercise.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import uk.co.imperatives.exercise.exception.NameValidationError;
import uk.co.imperatives.exercise.validation.NameValidator;

@ToString
@Getter
public class AddGuestRequest {
    private final String name;
    private int table;  // 0 by default, meaning no table provided
    private int accompanyingGuests;

    @Builder
    public AddGuestRequest(String name, int table, int accompanyingGuests) {
        if (name == null || name.isBlank()) {
            throw new NameValidationError("Name cannot be null or empty");
        }

        if (!NameValidator.isValidName(name)) {
            throw new NameValidationError(NameValidator.validate(name).message);
        }

        if (table < 0) {
            throw new IllegalArgumentException("table must be greater than zero");
        }

        if (accompanyingGuests < 0) {
            throw new IllegalArgumentException("accompanying guests cannot be negative");
        }

        this.name = name.trim();
        this.table = table;
        this.accompanyingGuests = accompanyingGuests;
    }

    // Method to check if a table was provided
    public boolean hasTable() {
        return table > 0;
    }

    public int noOfGuests() {
        return accompanyingGuests + 1;
    }
}
