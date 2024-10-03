package uk.co.imperatives.exercise.exception;

public class GuestHasLeftException extends RuntimeException {
    public GuestHasLeftException(String s) {
        super(s);
    }
}
