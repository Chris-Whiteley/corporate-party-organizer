package uk.co.imperatives.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddGuestRequestDto {
    private String name;
    private int table;  // 0 by default, meaning no table provided
    private int accompanyingGuests;
}
