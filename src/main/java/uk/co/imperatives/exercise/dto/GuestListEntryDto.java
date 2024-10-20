package uk.co.imperatives.exercise.dto;

import lombok.*;
import uk.co.imperatives.exercise.model.GuestListEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static GuestListEntryDto toDto(GuestListEntry guestListEntry) {
        return GuestListEntryDto.builder()
                .name(guestListEntry.getName())
                .tableNumber(guestListEntry.getTableNumber())
                .timeArrived(formatLocalDateTime(guestListEntry.getTimeArrived()))
                .timeLeft(formatLocalDateTime(guestListEntry.getTimeLeft()))
                .accompanyingGuests(guestListEntry.getAccompanyingGuests())
                .build();
    }

    private static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);
        }
        return "";
    }
}
