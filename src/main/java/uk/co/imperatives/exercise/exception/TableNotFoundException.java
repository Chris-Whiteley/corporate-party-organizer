package uk.co.imperatives.exercise.exception;

import jakarta.persistence.EntityNotFoundException;

public class TableNotFoundException extends EntityNotFoundException {
    public TableNotFoundException(String s) {
        super(s);
    }
}
