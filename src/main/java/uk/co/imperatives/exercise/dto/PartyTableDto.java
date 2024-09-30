package uk.co.imperatives.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyTableDto {
    private Integer number;
    private int noOfSeats;
    private int noOfSeatsAllocated;

    public int getUnAllocatedSeats() {
        return noOfSeats - noOfSeatsAllocated;
    }
}

