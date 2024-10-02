package uk.co.imperatives.exercise.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestListEntryDto {
    private String name;
    private int tableNumber;
    private LocalDateTime timeArrived;
    private int accompanyingGuests;
}
