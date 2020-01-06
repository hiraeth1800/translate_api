package be.geo_solutions.translate_api.exceptions;

public class DuplicateTranslationException extends RuntimeException {

    public DuplicateTranslationException(String message) {
        super(message);
    }
}
