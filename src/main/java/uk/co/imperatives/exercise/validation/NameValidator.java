package uk.co.imperatives.exercise.validation;

import java.util.regex.Pattern;

public class NameValidator {
    public enum ValidationResult {
        VALID ("name is valid"),
        EMPTY ("name is empty"),
        LENGTH_ERROR("name is too too long (greater than 100 characters)"),
        INVALID_CHARACTERS ("name contains invalid characters"),
        CONSECUTIVE_HYPHENS_OR_QUOTES ("name contains consecutive hyphens or quotes");

        public final String message;

        ValidationResult(String message) {
            this.message = message;
        }
    }

    // Define a regex pattern for valid names (allows letters, spaces, hyphens, and apostrophes)
    private static final String NAME_PATTERN = "^[a-zA-ZÀ-ÿ' -]+$";

    private static final int MAX_LENGTH = 100;

    public static boolean isValidName(String name) {
        return validate(name) == ValidationResult.VALID;
    }

    public static ValidationResult validate(String name) {
        String trimmedName = name.trim();

        if (trimmedName.isEmpty() || trimmedName.length() > MAX_LENGTH) {
            return ValidationResult.LENGTH_ERROR;
        }

        // Check if name matches the allowed character pattern
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        if (!pattern.matcher(trimmedName).matches()) {
            return ValidationResult.INVALID_CHARACTERS;
        }

        // Ensure no consecutive hyphens or apostrophes
        if (trimmedName.contains("--") || trimmedName.contains("''") || trimmedName.contains("  ")) {
            return ValidationResult.CONSECUTIVE_HYPHENS_OR_QUOTES;
        }

        return ValidationResult.VALID;
    }









}

