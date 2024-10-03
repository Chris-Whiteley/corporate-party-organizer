package uk.co.imperatives.exercise.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestListEntryDto {
    private String name;
    private int tableNumber;
    private String timeArrived;
    private String timeLeft;
    private int accompanyingGuests;
}
