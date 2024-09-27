package uk.co.imperatives.exercise.validation;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NameValidatorTests {

    @Test
    void testValidNames() {
        assertTrue(NameValidator.isValidName("John O'Connor"));
        assertTrue(NameValidator.isValidName("Marie-Claire"));
        assertTrue(NameValidator.isValidName(" José ")); // valid with trimming spaces
        assertTrue(NameValidator.isValidName("L")); // single letter valid
        assertTrue(NameValidator.isValidName("John ")); //  valid with trimming spaces
    }

    @Test
    void testInvalidNames() {
        assertFalse(NameValidator.isValidName("123John")); // contains numbers
        assertFalse(NameValidator.isValidName("O''Connor")); // consecutive apostrophes
        assertFalse(NameValidator.isValidName("Anne--Marie")); // consecutive hyphens
        assertFalse(NameValidator.isValidName("John#Doe")); // special character not allowed
        assertFalse(NameValidator.isValidName("")); // empty string
        assertFalse(NameValidator.isValidName("John  Doe")); // consecutive spaces
    }
}

