package uk.co.imperatives.exercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Version;


@Entity
@Table(name = "guest_list_entry")
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
public class GuestListEntry {
    @Id
    @Column(name = "name") // Assuming name is your primary key
    private String name;

    @Column(name = "table_number") // Renamed to avoid keyword conflict
    private int tableNumber;

    private int accompanyingGuests;
    @Version
    private Long version;

    public int noOfGuests() {
        return accompanyingGuests + 1;
    }
}