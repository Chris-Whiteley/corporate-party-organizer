package uk.co.imperatives.exercise.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;


@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guest {
    @Id
    private String name;
    private int table;
    private int accompanyingGuests;
}
