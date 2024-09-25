package uk.co.imperatives.exercise.service;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddGuestRequest {
    private final String name;
    private int table;  // 0 by default, meaning no table provided
    private int accompanyingGuests;

    // Method to check if a table was provided
    public boolean hasTable() {
        return table > 0;
    }
}
