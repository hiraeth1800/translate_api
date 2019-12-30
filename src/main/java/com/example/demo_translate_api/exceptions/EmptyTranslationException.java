package com.example.demo_translate_api.exceptions;

public class EmptyTranslationException extends RuntimeException {
    public EmptyTranslationException(String message) {
        super(message);
    }
}
