package uk.co.imperatives.exercise.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.imperatives.exercise.exception.NameValidationError;

public class AddGuestRequestValidationTests {

    @Test
    void shouldErrorWhenNameIsNull() {
        NameValidationError thrown = Assertions.assertThrows(NameValidationError.class, () -> {
            AddGuestRequest.builder().name(null).build();
        });

        Assertions.assertEquals("Name cannot be null or empty", thrown.getMessage());
    }

    @Test
    void shouldErrorWhenNameIsEmpty() {
        NameValidationError thrown = Assertions.assertThrows(NameValidationError.class, () -> {
            AddGuestRequest.builder().name("  ").build();
        });

        Assertions.assertEquals("Name cannot be null or empty", thrown.getMessage());
    }

    @Test
    void shouldErrorWhenNegativeAccompanyingGuests() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AddGuestRequest.builder().name("some name ").accompanyingGuests(-1).build();
        });

        Assertions.assertEquals("accompanying guests cannot be negative", thrown.getMessage());
    }

    @Test
    void shouldErrorWhenNegativeTableNumber() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AddGuestRequest.builder().name("name").table(-1).build();
        });

        Assertions.assertEquals("table must be greater than zero", thrown.getMessage());
    }

}
