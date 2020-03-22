package de.cketti.mailto;

import androidx.annotation.NonNull;

/**
 * Thrown when parsing failed.
 */
public class ParseException extends RuntimeException {
    public String response;

    ParseException(@NonNull String response) {
        super(response);
        this.response = response;
    }
}
