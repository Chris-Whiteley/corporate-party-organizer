package uk.co.imperatives.exercise.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Version;


@Entity
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "number")
public class PartyTable {
    @Id
    private Integer number;
    private int noOfSeats;
    private int noOfSeatsAllocated;

    @Builder.Default
    @Version
    private Long version = 0L;

    public int getUnAllocatedSeats() {
        return noOfSeatsAllocated - noOfSeats;
    }
}