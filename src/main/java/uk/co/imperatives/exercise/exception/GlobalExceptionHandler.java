package uk.co.imperatives.exercise.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleEOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        String message = "Database locking error, someone else has updated this record, please try again!";
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TableAlreadyExistsException.class)
    public ResponseEntity<String> handleTableAlreadyExistsException(TableAlreadyExistsException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TableInUseException.class)
    public ResponseEntity<String> handleTableInUseException(TableInUseException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
