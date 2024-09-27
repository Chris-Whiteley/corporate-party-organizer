package uk.co.imperatives.exercise.exception;

public class GuestNotFoundException extends RuntimeException {
    public GuestNotFoundException(String s) {
        super(s);
    }
}
