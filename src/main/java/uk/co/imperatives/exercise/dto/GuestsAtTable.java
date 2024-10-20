package uk.co.imperatives.exercise.dto;

import lombok.*;

import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestsAtTable {
    private int tableNumber;
    @Singular
    private Collection<GuestListEntryDto> guests;
}
