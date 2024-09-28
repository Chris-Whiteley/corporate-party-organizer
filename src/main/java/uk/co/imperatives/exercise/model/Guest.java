package uk.co.imperatives.exercise.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.Version;


@Entity
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class Guest {
    @Id
    private String name;
    private int table;
    private int accompanyingGuests;
    @Version
    private Long version;

    public int noOfGuests() {
        return accompanyingGuests + 1;
    }
}