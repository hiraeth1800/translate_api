package be.geo_solutions.translate_api.exceptions;

public class EmptyTranslationException extends RuntimeException {
    public EmptyTranslationException(String message) {
        super(message);
    }
}
