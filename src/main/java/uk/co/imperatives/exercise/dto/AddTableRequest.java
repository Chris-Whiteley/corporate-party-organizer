package uk.co.imperatives.exercise.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddTableRequest {
    private Integer tableNumber;
    private Integer noOfSeats;
}