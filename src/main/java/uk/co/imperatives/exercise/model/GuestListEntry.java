package uk.co.imperatives.exercise.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;


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

    @Column(name = "time_arrived")
    private LocalDateTime timeArrived;

    @Column(name = "time_left")
    private LocalDateTime timeLeft;

    @Setter
    private int accompanyingGuests;
    @Version
    private Long version;

    public int noOfGuests() {
        return accompanyingGuests + 1;
    }

    public void recordTimeArrived() {
        this.timeArrived = LocalDateTime.now();
    }

    public void recordTimeLeft() {
        this.timeLeft = LocalDateTime.now();
    }

    public boolean hasLeft() {
        return this.timeLeft != null;
    }
}