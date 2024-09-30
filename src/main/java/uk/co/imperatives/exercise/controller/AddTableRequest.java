package uk.co.imperatives.exercise.controller;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddTableRequest {
    private Integer tableNumber;
    private Integer noOfSeats;
}
