package uk.co.imperatives.exercise.exception;

public class GuestAlreadyExistsException extends RuntimeException {
    public GuestAlreadyExistsException(String s) {
        super(s);
    }
}
