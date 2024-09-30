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
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "table_seq")
    @SequenceGenerator(name = "table_seq", sequenceName = "table_sequence", allocationSize = 1)
    private Integer number;
    private int noOfSeats;
    private int noOfSeatsAllocated;
    @Version
    private Long version;

    public int getUnAllocatedSeats() {
        return noOfSeatsAllocated - noOfSeats;
    }
}