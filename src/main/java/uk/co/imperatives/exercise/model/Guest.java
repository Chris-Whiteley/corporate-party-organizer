package uk.co.imperatives.exercise.model;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
    private String name;
    private int table;
    private int accompanyingGuests;
}
